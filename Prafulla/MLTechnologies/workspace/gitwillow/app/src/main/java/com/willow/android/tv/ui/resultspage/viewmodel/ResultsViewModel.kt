package com.willow.android.tv.ui.resultspage.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.resultspage.datamodel.APIResultsDataModel
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.resultspage.model.ResultsDateListModel
import com.willow.android.tv.ui.resultspage.model.ResultsWrapperDataModel
import com.willow.android.tv.utils.DateModel
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ResultsViewModel(application: Application, private val navData : NavigationTabsDataModel?) : BaseAndroidViewModel(application) {

    var selectedPos = 0

    private val _posObserve = MutableLiveData<Int>()
    val posObserve: LiveData<Int> = _posObserve

    private val _renderPage = MutableLiveData<Resource<APIResultsDataModel>>()
    val renderPage: LiveData<Resource<APIResultsDataModel>> = _renderPage

    private val _selectedPosition = MutableLiveData<String>()
    val selectedPosition : LiveData<String> =_selectedPosition

    val _targetUrl = SingleLiveEvent<String>()



    private val _datesList = MutableLiveData<List<ResultsDateListModel>?>()
    val datesList : MutableLiveData<List<ResultsDateListModel>?> =_datesList

    private val _matchesList = MutableLiveData<ResultsWrapperDataModel>()
    val matchesList : MutableLiveData<ResultsWrapperDataModel> =_matchesList

    init {
        _posObserve.value = selectedPos
        _selectedPosition.value = getMonthsUpcoming()[0].monthYearFormatted
        loadFixturesPageConfig()
    }


    private fun loadFixturesPageConfig() {
        viewModelScope.launch (Dispatchers.IO){
            _renderPage.postValue(Resource.Loading())
            fetchFixturesPageDetails()
        }
    }

    private suspend fun fetchFixturesPageDetails() {
        val data =  RepositoryFactory.getResultsPageRepository().getResultsPageData(getApplication(),true)

//        Timber.d("FixturesAPI " +data.getResultsPageConfig().data.toString())
        _renderPage.postValue(data.getResultsPageConfig())

    }


    fun nextButtonClicked(){
        if(selectedPos>0){
            selectedPos--
            _posObserve.value = selectedPos
            getDatesList()
        }
    }

    fun previousButtonClicked(){

        if(selectedPos <11) {
            selectedPos++
            _posObserve.value = selectedPos
            getDatesList()
        }
    }

    fun getMatchesList(startDate: String?){
        when(_renderPage.value){
            is Resource.Success ->{
                var apiData = _renderPage.value?.data?.result?.results_by_date
                apiData = apiData?.sortedByDescending { it.gmt_start_date_ts }

                val matches = apiData?.filter {
                    it.getLocalTimeFromGmtTs().contains(startDate?:"XXX") }

                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else{
                    val resultsWrapperDataModel = ResultsWrapperDataModel(false,matches)
                    _matchesList.postValue(resultsWrapperDataModel)

                }
            }
            else ->{
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }

        }
    }


    fun getAllMatchesList(startDate: String?){
        when(_renderPage.value){
            is Resource.Success ->{
                var apiData = _renderPage.value?.data?.result?.results_by_date
                apiData = apiData?.sortedByDescending { it.gmt_start_date_ts }

                val matches = apiData?.filter {
                    it.getMonthFromGmtTs().contains(startDate?:"XXX") }

                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else {
                    val resultsWrapperDataModel = ResultsWrapperDataModel(true,matches)
                    _matchesList.postValue(resultsWrapperDataModel)
                }
            }
            else ->{
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }

        }
    }
    fun getDatesList(){
        val monthsList = getMonthsUpcoming()
        Timber.d(monthsList.toString())

        _selectedPosition.value = monthsList[selectedPos].monthYearFormatted

        when(_renderPage.value){
            is Resource.Success ->{
                var apiData = _renderPage.value?.data?.result?.results_by_date
                apiData = apiData?.sortedByDescending { it.gmt_start_date_ts }
                val dates = apiData?.map {
                    ResultsDateListModel(it.getLocalTimeFromGmtTs().substringBefore(" : "),it.gmt_start_date,false)
                }?.filter {
                    it.gmt_start_date.contains(monthsList[selectedPos].monthYear)

                }?.distinctBy {
                    it.pst_start_date
                }
                if(dates?.isEmpty() == false) {
                    val month = _selectedPosition.value
                    getAllMatchesList(month)
                }else{
                    getMatchesList(null)
                }
                _datesList.postValue(dates)
            }
            else -> {}
        }
    }


    private fun getMonthsUpcoming(): MutableList<DateModel>{
        val currentDate = LocalDate.now()
        val upcomingMonths = mutableListOf<DateModel>()
        for (i in 0..12) {
            val nextMonth = currentDate.minusMonths(i.toLong())
            val format = DateTimeFormatter.ofPattern("yyyy-MM")
            val formattedMonth = nextMonth.format(format)
            val nextMonthFormatted = currentDate.minusMonths(i.toLong()).month.toString()+" "+currentDate.minusMonths(i.toLong()).year.toString()

            upcomingMonths.add(
                DateModel(formattedMonth,nextMonthFormatted.lowercase().replaceFirstChar(Char::titlecase),
                i==12
            )
            )
        }
        return upcomingMonths
    }

}
