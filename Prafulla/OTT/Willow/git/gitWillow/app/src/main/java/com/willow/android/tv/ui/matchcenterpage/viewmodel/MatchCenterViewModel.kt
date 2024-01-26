package com.willow.android.tv.ui.matchcenterpage.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.common.base.BaseAndroidViewModel
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.data.repositories.commondatamodel.Thumbnails
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.APINewMatchCenterDataModel
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.Tab
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import com.willow.android.tv.ui.matchcenterpage.model.MatchCenterPageModel
import com.willow.android.tv.ui.matchcenterpage.model.TabsDataModel
import com.willow.android.tv.ui.matchcenterpage.model.UpcomingMatchesWrapperDataModel
import com.willow.android.tv.utils.DateModel
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MatchCenterViewModel(application: Application) : BaseAndroidViewModel(application) {

    var selectedPos = 0

    private val _posObserve = MutableLiveData<Int>()
    val posObserve: LiveData<Int> = _posObserve

    private val _selectedPosition = MutableLiveData<String>()
    val selectedPosition: LiveData<String> = _selectedPosition

    val _targetUrl = SingleLiveEvent<String>()

    private val _datesList = MutableLiveData<List<DateListModel>?>()
    val datesList: MutableLiveData<List<DateListModel>?> = _datesList

    private val _matchesList = MutableLiveData<UpcomingMatchesWrapperDataModel>()
    val matchesList: MutableLiveData<UpcomingMatchesWrapperDataModel> = _matchesList

    private val _renderPage = MutableLiveData<Resource<APINewMatchCenterDataModel>>()
    val renderPage: LiveData<Resource<APINewMatchCenterDataModel>> = _renderPage

    val _urlToBeCalled = MutableLiveData<String>()
    val urlToBeCalled: LiveData<String> = _urlToBeCalled

    private val _cardRowsList = MutableLiveData<MatchCenterPageModel?>()
    val cardRowsList: MutableLiveData<MatchCenterPageModel?> = _cardRowsList


    init {
        _posObserve.value = selectedPos
        _selectedPosition.value = getMonthsUpcoming()[0].monthYearFormatted
    }

    fun loadMatchCenterPageConfig() {
        viewModelScope.launch(Dispatchers.IO) {
            _renderPage.postValue(Resource.Loading())
            fetchMatchCenterPageDetails()
        }
    }

    private suspend fun fetchMatchCenterPageDetails() {
        val data = RepositoryFactory.getMatchCenterPageRepository()
            .getMatchCenterPageData(getApplication(), true)

//        _renderPage.postValue(data.getMatchCenterPageConfig(_urlToBeCalled.value.toString()))
        _renderPage.postValue(data.getMatchCenterPageConfigNew(_urlToBeCalled.value.toString()))

    }


    fun getTabNames(): ArrayList<TabsDataModel> {
        val tabsList = ArrayList<TabsDataModel>()
        val apiData = _renderPage.value?.data?.result?.detail?.tabs
        if (!apiData.isNullOrEmpty()) {
            apiData.forEach {
                tabsList.add(TabsDataModel(it.title, it.tab_type.toString(), it.getUrl()))
            }
        } else {
            //todo("Declare error livedata and show error ")
        }
        Timber.d("##### apiData:: $apiData")
        Timber.d("##### TabsList:: $tabsList")

        return tabsList
    }

    fun getTabTrageUrlList(): List<Tab>? {
        return _renderPage.value?.data?.result?.detail?.tabs
    }

    suspend fun getVideosRows() {
        val cardRows = ArrayList<CardRow>()
        val apiData = _renderPage.value?.data
        if ((apiData?.result?.detail?.tabs != null) || (apiData?.result?.detail?.tabs?.isEmpty() == false)) {
            val rows = apiData.result.detail.tabs[0].rows
            if (!rows.isNullOrEmpty()) {
                rows.forEach {
                    val cards = ArrayList<Card>()
                    if(it.items!=null){
                        it.items?.forEach { content ->
                            cards.add(
                                Card(
                                    title = content.title,
                                    sub_title = content.sub_title,
                                    duration = content.duration,
                                    duration_seconds = content.duration_seconds,
                                    img_path = content.img_path,
                                    display_live_link = content.display_live_link,
                                    need_login = content.need_login,
                                    need_subscription = content.need_subscription,
                                    isExpandable = false,
                                    base_url_type = content.base_url_type,
                                    target_url = content.target_url,
                                    thumbnails = Thumbnails(
                                        content.thumbnails.hrb,
                                        content.thumbnails.lgb,
                                        content.thumbnails.mdb,
                                        content.thumbnails.ptr_lgb,
                                        content.thumbnails.ptr_mdb,
                                        content.thumbnails.ptr_smb
                                    ),
                                    target_action = content.target_action,
                                    target_type = content.target_type,
                                    content_type = content.content_type,
                                    content_details = content.content_details,
                                    content_id = content.content_id,
                                    event_id = content.event_id
                                )
                            )
                        }
                    }else if(it.content!=null){
                        it.content?.forEach { content ->
                            Timber.d("TESTTTESTT content:: $content")
                            cards.add(
                                Card(
                                    title = content.title,
                                    sub_title = content.sub_title,
                                    duration = content.duration,
                                    duration_seconds = content.duration_seconds,
                                    img_path = content.img_path,
                                    display_live_link = content.display_live_link,
                                    need_login = content.need_login,
                                    need_subscription = content.need_subscription,
                                    isExpandable = false,
                                    thumbnails = Thumbnails(
                                        content.thumbnails.hrb,
                                        content.thumbnails.lgb,
                                        content.thumbnails.mdb
                                    ),
                                    target_action = content.target_action,
                                    target_type = content.target_type,
                                    base_url_type = content.base_url_type,
                                    target_url = content.target_url,
                                    content_type = content.content_type,
                                    content_details = content.content_details,
                                    content_id = content.content_id,
                                    event_id = content.event_id
                                )
                            )
                        }
                    }

                    cardRows.add(
                        CardRow(
                            title = it.title.replaceFirstChar { title ->
                                if (title.isLowerCase()) title.titlecase(
                                    Locale.ROOT
                                ) else title.toString()
                            },
                            sub_title = "",
                            items_category = "video",
                            items = cards,
                            card_type = if (it.card_type != null) it.card_type.toString() else
                                if (it.items_category != null) it.items_category.toString() else it.content_type.toString()
                        )
                    )
                }
                cardRows.reverse()
                val matchCenterPageModel = MatchCenterPageModel(
                    cardRowModel = cardRows,
                    apiData.result.detail.title?:apiData.result.detail.series_name,
                    apiData.result.detail.formatMMMDDStartAndEndTime(apiData.result.detail.just_show_start_date)
                )
                _cardRowsList.postValue(matchCenterPageModel)
            } else {
                _cardRowsList.postValue(null)
            }
        }
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
                var apiData = _renderPage.value?.data?.result?.detail?.tabs?.get(1)?.items
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }

                val matches = apiData?.filter {
                    it.getLocalTimeFromGmtTs().contains(startDate ?: "XXX")
                }
                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else {
                    val matchesWrapperDataModel = UpcomingMatchesWrapperDataModel(false,matches)
                    _matchesList.postValue(matchesWrapperDataModel)
                }
            }
            else -> {
                _errorPageShow.value = ErrorType.NO_MATCH_FOUND
            }
        }
    }
    fun getAllMatchesToList(startDate:String?){
        when (_renderPage.value) {
            is Resource.Success -> {
                var apiData = _renderPage.value?.data?.result?.detail?.tabs?.get(1)?.items
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }
                val matches = apiData?.filter {
                    it.getMonthFromGmtTs().contains(startDate ?: "XXX")
                }
                if (matches.isNullOrEmpty())
                    _errorPageShow.value = ErrorType.NO_MATCH_FOUND
                else  {
                    val matchesWrapperDataModel = UpcomingMatchesWrapperDataModel(true,matches)
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
                var apiData = _renderPage.value?.data?.result?.detail?.tabs?.get(1)?.items
                apiData = apiData?.sortedBy { it.gmt_start_date_ts }

                val dates = apiData?.map {
                    DateListModel(
                        it.getLocalTimeFromGmtTs().substringBefore(" : ") ,
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
