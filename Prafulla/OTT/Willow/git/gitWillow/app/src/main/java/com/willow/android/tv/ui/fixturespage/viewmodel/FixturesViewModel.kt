package com.willow.android.tv.ui.fixturespage.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.fixturespage.datamodel.APIFixturesDataModel
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesByDate
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import com.willow.android.tv.ui.fixturespage.model.MatchesWrapperDataModel
import com.willow.android.tv.utils.DateModel
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FixturesViewModel(application: Application, private val navData: NavigationTabsDataModel?) :
    BaseAndroidViewModel(application) {

    var selectedPos = 0

    private val _posObserve = MutableLiveData<Int>()
    val posObserve: LiveData<Int> = _posObserve

    private val _renderPage = MutableLiveData<Resource<APIFixturesDataModel>>()
    val renderPage: LiveData<Resource<APIFixturesDataModel>> = _renderPage

    private val _selectedPosition = MutableLiveData<String>()
    val selectedPosition: LiveData<String> = _selectedPosition


    val _targetUrl = SingleLiveEvent<String>()


    private val _datesList = SingleLiveEvent<List<DateListModel>?>()
    val datesList: SingleLiveEvent<List<DateListModel>?> = _datesList

    private val _matchesList = MutableLiveData<MatchesWrapperDataModel>()
    val matchesList: MutableLiveData<MatchesWrapperDataModel> = _matchesList

    init {
        _posObserve.value = selectedPos
        _selectedPosition.value = getMonthsUpcoming()[0].monthYearFormatted
        loadFixturesPageConfig()
    }


    fun loadFixturesPageConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            _renderPage.postValue(Resource.Loading())
            fetchFixturesPageDetails()
        }
    }

    private suspend fun fetchFixturesPageDetails() {
        val data = RepositoryFactory.getFixturesPageRepository()
            .getFixturesPageData(getApplication(), true)

//        Timber.d("FixturesAPI " +data.getFixturesPageConfig().data.toString())
        _renderPage.postValue(data.getFixturesPageConfig())

    }


    fun nextButtonClicked() {
        if (selectedPos < 11) {
            selectedPos++
            _posObserve.value = selectedPos
            getDatesList()
        }
    }

    fun previousButtonClicked() {
        if (selectedPos > 0) {
            selectedPos--
            _posObserve.value = selectedPos
            getDatesList()
        }
    }

    fun getMatchesList(startDate: String?) {

        when (_renderPage.value) {
            is Resource.Success -> {
                var apiData = _renderPage.value?.data?.result?.fixtures_by_date
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }
                val matches = apiData?.filter {
                    it.getLocalTimeFromGmtTs().contains(startDate ?: "XXX")
                }
                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else {
                    val matchesWrapperDataModel = MatchesWrapperDataModel(false,matches)
                    _matchesList.postValue(matchesWrapperDataModel)
                }
            }
            else -> {
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }
        }

    }

    fun getFilteredMatchesList(startDate: String?): MatchesWrapperDataModel?{

        when (_renderPage.value) {
            is Resource.Success -> {
                var apiData = _renderPage.value?.data?.result?.fixtures_by_date
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }
                val matches = apiData?.filter {
                    it.getLocalTimeFromGmtTs().contains(startDate ?: "XXX")
                }
                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else {
                   return MatchesWrapperDataModel(false,matches)
                }
            }
            else -> {
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }
        }
        val matches = ArrayList<FixturesByDate>()

        return MatchesWrapperDataModel(false,matches)

    }

    fun getAllMatchesToList(startDate:String?){
        when (_renderPage.value) {
            is Resource.Success -> {
                var apiData = _renderPage.value?.data?.result?.fixtures_by_date
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }
                val matches = apiData?.filter {
                    it.getMonthFromGmtTs().contains(startDate ?: "XXX")
                }
                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else {
                    val matchesWrapperDataModel = MatchesWrapperDataModel(true,matches)
                    _matchesList.postValue(matchesWrapperDataModel)
                }
            }
            else -> {
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }
        }
    }
    fun getDatesList() {
        val monthsList = getMonthsUpcoming()

        _selectedPosition.value = monthsList[selectedPos].monthYearFormatted

        when (_renderPage.value) {
            is Resource.Success -> {
                var apiData = _renderPage.value?.data?.result?.fixtures_by_date
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }
                val dates = apiData?.map {
                    DateListModel(
                        it.getLocalTimeFromGmtTs().substringBefore(" : "),
                        it.gmt_start_date,
                        false
                    )
                }?.filter {
                    it.gmt_start_date.contains(monthsList[selectedPos].monthYear)

                }
                if (dates?.isEmpty() == false) {
//                    dates.get(0).selected = true
                    val month = _selectedPosition.value
                    getAllMatchesToList(month)
                } else {
                    getMatchesList(null)
                }
                _datesList.postValue(dates)
            }
            else -> {}
        }
    }


    private fun getMonthsUpcoming(): MutableList<DateModel> {
        val currentDate = LocalDate.now()
        val upcomingMonths = mutableListOf<DateModel>()
        for (i in 0..12) {
            val nextMonth = currentDate.plusMonths(i.toLong())
            val format = DateTimeFormatter.ofPattern("yyyy-MM")
            val formattedMonth = nextMonth.format(format)
            val nextMonthFormatted =
                currentDate.plusMonths(i.toLong()).month.toString() + " " + currentDate.plusMonths(i.toLong()).year.toString()

            upcomingMonths.add(
                DateModel(
                    formattedMonth,
                    nextMonthFormatted.lowercase().replaceFirstChar(Char::titlecase),
                    i == 1
                )
            )
        }
        return upcomingMonths
    }

}
