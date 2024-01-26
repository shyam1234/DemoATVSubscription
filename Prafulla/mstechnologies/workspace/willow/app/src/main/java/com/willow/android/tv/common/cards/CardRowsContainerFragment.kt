package com.willow.android.tv.common.cards

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.leanback.app.RowsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.BaseOnItemViewSelectedListener
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.lifecycleScope
import com.willow.android.WillowApplication
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.cards.presenters.PresenterSelector
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.data.room.db.VideoProgressDao
import com.willow.android.tv.utils.GlobalConstants.DELAY_IN_CARD_EXPAND_ANIM
import com.willow.android.tv.utils.events.ContinueWatchingDeleteEvent
import com.willow.android.tv.utils.events.ContinueWatchingEvent
import com.willow.android.tv.utils.events.LiveRefreshEvent
import com.willow.android.tv.utils.extension.toArrayList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


/**
 * this class is used for rendering the CardRows
 */
class CardRowsContainerFragment() : RowsSupportFragment(),
    BaseOnItemViewSelectedListener<CardListRow> {

    private var selectedRowListener: BaseOnItemViewSelectedListener<CardListRow>? = null
    private var cardRowList: ArrayList<CardRow>? = null
    private var rowList: ArrayList<Any>? = null
    private var listener: KeyListener? = null
    private var lastSelectedPosition: Int = 0
    private var focusFirstItem:Boolean?=true
    private var expandCardJob: Runnable? = null
    private var lastSelectedItem: Card? = null
    private var lastSelectedRow: CardListRow? = null
    private val expandCardHandler = Handler(Looper.myLooper()!!)
    private lateinit var videoProgressDao:VideoProgressDao
    private lateinit var rowsAdapter:CustumArrayObjectAdapter
    var selectedItem: Card? = null

    companion object {
        @JvmStatic
        fun newInstance(selectedRowListener: BaseOnItemViewSelectedListener<CardListRow>?,cardRowList: ArrayList<CardRow>?,listener: KeyListener?, focusFirstItem: Boolean? =true) =
            CardRowsContainerFragment().apply {
                this.selectedRowListener = selectedRowListener
                this.cardRowList = cardRowList
                this.listener = listener
                this.focusFirstItem = focusFirstItem
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRowAdapter()
        onItemViewSelectedListener = this
        videoProgressDao = WillowApplication.dbBuilder.videoProgressDao()
        if (focusFirstItem == true) {
            focusFirstItem()
        }
    }

    private fun setupRowAdapter() {
        val listRowPresenter = ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE)
        listRowPresenter.selectEffectEnabled =false
        rowsAdapter = CustumArrayObjectAdapter(listRowPresenter)
        createRows()
        adapter = rowsAdapter
    }

    private fun createRows() {
        cardRowList?.forEach {
            val row = createCardRow(it)
            if (row != null) {
                rowsAdapter.add(row)
            }
        }
    }

    private fun createRowList(){
        rowList?.clear()
        cardRowList?.forEach {
            val row = createCardRow(it)
            if (row != null) {
                rowList?.add(row)
            }
        }
    }

    fun refreshLiveRow(cardRowList: ArrayList<CardRow>){
        this.cardRowList = cardRowList
        createRowList()
        rowList?.let { rowsAdapter.setItems(it) }
    }


    private fun createCardRow(cardRow: CardRow): Row? {
        val whiteListedItems: ArrayList<Card> = ArrayList()
        return cardRow.items?.let { items ->
            if (items.isNotEmpty() && listener != null) {
                val presenter = PresenterSelector.getPresenter(cardRow, listener!!)
                presenter?.let { it ->
                    val rowAdapter = ArrayObjectAdapter(it)
                    for ((index, item) in items.filter { it.isItemWhitelist() /*&& it.isValidLiveContent() */}.withIndex()) {
                        item.index = index
                        rowAdapter.add(item)
                        whiteListedItems.add(item)
                    }
                    if (rowAdapter.size() > 0) {
                        cardRow.items = whiteListedItems
                        CardListRow(HeaderItem(cardRow.title), rowAdapter, cardRow)
                    } else {
                        null
                    }
                }
            } else {
                null
            }
        }
    }

    /**
     * Method to update the Continue watching row if the user came back to Home from playing a video
     * Called only when the ContinueWatchingEvent is triggered (whenever a new video is added to VideoProgress Table)
     * 1. Iterates through the global cardRowList and does the db operation to find out the Videos that have to be added to Continue watching
     * 2. If title of the row matches Continue Watching the video list from the db operation (cardContinue) will be added to that row.
     * 3. A row will be created using that row later
     * 4. if there is only 1 item in the cardContinueRow i.e; there was not Continue Watching Row earlier and we have to add it in CONTINUE_WATCHING_POS
     * row will be added to rowAdapter in that case, else the existing continue watching row will be replaced by new one.
     * */
    private fun updateCardRowListWithContinueWatching(videoAdded: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            var cardContinue= ArrayList<Card>()
            //1
            cardRowList?.forEach { cardRow ->

                cardRow.items?.forEachIndexed { index,   card ->
                    val videoProgress = card.content_id?.let { videoProgressDao.getVideoProgressByVideoId(it)}
                    if(videoProgress !=null){
                        card.progress = videoProgress.progress

                        cardContinue.add(card.copy())
                    }
                }
            }

            cardContinue = cardContinue.distinctBy { it.title }.toArrayList()
            cardContinue.reverse()

//            var continueWatchRow : CardRow?=null

            val livePresent: Boolean = cardRowList?.get(1)?.items?.isEmpty()==false

            val continueWatchPos = getContinueWatchingPos(livePresent)

            val continueWatchRow = CardRow(title = "Continue Watching", sub_title = "", card_type = "medium_landscape", items_category = "video", items = cardContinue)

            if((cardRowList?.size ?: 0) > 2) {
                if (continueWatchRow.items?.isEmpty() == false) {
                    if ((cardRowList?.get(2)?.items?.size ?: 0) > 0) {
                        createCardRow(continueWatchRow)?.let {
                            rowsAdapter.replace(
                                continueWatchPos,
                                it
                            )
                        }
                    } else {
                        createCardRow(continueWatchRow)?.let {
                            rowsAdapter.add(
                                continueWatchPos,
                                it
                            )
                        }
                    }
                } else {
                    rowsAdapter.removeItems(continueWatchPos, 1)
                }
            }
        }
    }

    private fun getContinueWatchingPos(livePresent: Boolean):Int {
        return if(livePresent){
            1
        }else{
            0
        }

    }


    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: CardListRow?
    ) {

        lastSelectedPosition = selectedPosition
        selectedRowListener?.onItemSelected(itemViewHolder, item, rowViewHolder, row)
        val card = item as? Card ?: return
        selectedItem = card
        when (row?.getCardRow()?.getCardType()) {
            Types.Card. PORTRAIT_TO_LANDSCAPE -> {
                val card = item as? Card ?: return
                lastSelectedItem = card
                lastSelectedRow = row
                handleExpandableCardAnim(row, card)
            }
            Types.Card. EXPANDED_LANDSCAPE -> {
                val card = item as? Card ?: return
                lastSelectedItem = card
                lastSelectedRow = row
                handleExpandableCardAnim(row, card, 0)
            }
            else -> {
                // Reset the previously expanded item, if any
                lastSelectedItem?.let { selectedItem ->
                    if (selectedItem.isExpandable) {
                        selectedItem.isExpandable = false
                        lastSelectedRow?.adapter?.notifyItemRangeChanged(
                            0,
                            lastSelectedRow?.adapter?.size() ?: 0
                        )
                    }
                }
            }
        }
    }

    private fun handleExpandableCardAnim(row: CardListRow, card: Card?, delay: Long = DELAY_IN_CARD_EXPAND_ANIM) {
        row.getCardRow().items?.forEach {
            if (it != card) {
                it.isExpandable = false
            }
        }
        card?.let { c ->
            if (!c.isExpandable) {
                c.isExpandable = true
                expandCardJob?.let {
                    expandCardHandler.removeCallbacks(it)
                    expandCardJob = null
                }
                expandCardJob = Runnable {
                    row.adapter.notifyItemRangeChanged(0, row.adapter.size())
                    expandCardJob = null
                }
                expandCardJob?.let { obj ->
                    expandCardHandler.postDelayed(obj, delay)
                }
            }
        }
    }

    fun focusFirstItem() {
        lifecycleScope.launch {
            delay(200)
            view?.requestFocus()
            selectedPosition = lastSelectedPosition
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ContinueWatchingEvent) {

        Timber.d("#### ContinueWatchingEvent :: "+event.videoAdded)

        updateCardRowListWithContinueWatching(event.videoAdded)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LiveRefreshEvent) {

        Timber.d("#### LiveRefreshEvent :: "+event.cardRow)

        refreshLiveRow(event.cardRow)
    }
    @Subscribe(sticky = true)
    fun onMessageEvent(event: ContinueWatchingDeleteEvent) {

        Timber.d("#### ContinueWatchingDeleteEvent ::"+event.videoAdded)

        if(rowsAdapter.hasStableIds()){
            updateCardRowListWithContinueWatching(event.videoAdded)
        }else{
            Timber.d("#### ContinueWatchingDeleteEvent ::No Stable IDs")

//            EventBus.getDefault().postSticky(ContinueWatchingDeleteEvent(false))
        }


    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        EventBus.getDefault().unregister(this)
        super.onDetach()
    }
}