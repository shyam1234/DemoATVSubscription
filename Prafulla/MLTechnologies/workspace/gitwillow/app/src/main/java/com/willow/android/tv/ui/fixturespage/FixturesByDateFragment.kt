package com.willow.android.tv.ui.fixturespage

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.willow.android.R
import com.willow.android.databinding.FragmentFixturesByDateBinding
import com.willow.android.databinding.FragmentMatchCenterVideosTabBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.ContentDetail
import com.willow.android.tv.data.repositories.commondatamodel.Thumbnails
import com.willow.android.tv.data.repositories.fixturespage.datamodel.FixturesByDate
import com.willow.android.tv.ui.fixturespage.adapters.DatesAdapter
import com.willow.android.tv.ui.fixturespage.adapters.FixturesWatchLiveItemClickListener
import com.willow.android.tv.ui.fixturespage.adapters.ItemClickListener
import com.willow.android.tv.ui.fixturespage.adapters.MatchesItemClickListener
import com.willow.android.tv.ui.fixturespage.adapters.MatchesListAdapter
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import com.willow.android.tv.ui.fixturespage.viewmodel.FixturesViewModel
import com.willow.android.tv.ui.login.LoginActivity
import com.willow.android.tv.ui.playback.PlaybackActivity
import com.willow.android.tv.ui.subscription.SubscriptionActivity
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GoTo
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.events.CardClickedEvent
import com.willow.android.tv.utils.events.FixturesTabKeyDownEvent
import com.willow.android.tv.utils.events.MatchCenterDestroyed
import com.willow.android.tv.utils.events.NavMenuToggleEvent
import com.willow.android.tv.utils.extension.startActivityWithData
import com.willow.android.tv.utils.extension.startActivityWithOutData
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [FixturesByDateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FixturesByDateFragment : BaseFragment(), ItemClickListener, MatchesItemClickListener,
    FixturesWatchLiveItemClickListener, KeyListener {

    private lateinit var mViewModel: FixturesViewModel
    private var mBinding: FragmentFixturesByDateBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var dateListPos: Int = 0
    private var clickedMatchCenterPos: Int = -1
    private var selectedDate: String =""


    private lateinit var keyListener: KeyListener

    private lateinit var  fixturesByDateFragment : FixturesByDateFragment

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentFixturesByDateBinding.inflate(
            inflater.cloneInContext(context),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoader(binding.loadingview.layoutLoading)

        mViewModel = ViewModelProviders.of(requireParentFragment())[FixturesViewModel::class.java]

        val adapterDatesRv = DatesAdapter(this)
        val adapterMatchesRv = MatchesListAdapter(this, this, this)

        binding.apply {
            datesList.adapter = adapterDatesRv
            matchesList.adapter = adapterMatchesRv
            matchesList.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    val firstItem = matchesList.findViewHolderForAdapterPosition(0)
                    val firstItemView = firstItem?.itemView
                    val firstItemChild1 = firstItemView?.findViewById<View>(R.id.buttonWatchLive)
                    val firstItemChild2 = firstItemView?.findViewById<View>(R.id.buttonMatchCenter)

                    if (firstItemChild1?.hasFocus() == true || firstItemChild2?.hasFocus() == true ) {
                        matchesList.scrollToPosition(0)
                    }
                }
            }

            layoutMonthSelector.btnNext.setOnClickListener {
                selectedDate =""
                mViewModel.nextButtonClicked()
            }
            layoutMonthSelector.btnPrevious.setOnClickListener {
                selectedDate = ""
                mViewModel.previousButtonClicked()
            }

            layoutMonthSelector.btnNext.setOnKeyListener { v, keyCode, event ->
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (binding.datesList.visibility == View.GONE) {
                            Timber.d("datesListForFocus isEmpty")
                            EventBus.getDefault().post(NavMenuToggleEvent(true))

                        } else {
                            keyListener.onKey(v, keyCode, event)
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (binding.matchesList.visibility == View.GONE) {
                            Timber.d("matchesList isEmpty")

                            layoutMonthSelector.btnNext.requestFocus()
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        keyListener.onKey(v, keyCode, event)
                        return@setOnKeyListener true
                    }

                }
                false
            }

            layoutMonthSelector.btnPrevious.setOnKeyListener { v, keyCode, event ->
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (binding.datesList.visibility == View.GONE) {
                            Timber.d("datesListForFocus2 isEmpty")
                            EventBus.getDefault().post(NavMenuToggleEvent(true))
                        } else {
                            keyListener.onKey(v, keyCode, event)
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        keyListener.onKey(v, keyCode, event)
                        return@setOnKeyListener true
                    }

                }
                false
            }
        }

        mViewModel.apply {
            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        mViewModel.getDatesList()
                    }
                    is Resource.Error -> {
                        showErrorPage(ErrorType.NONE, it.message)
                    }
                    else -> {

                    }
                }
            }

            selectedPosition.observe(viewLifecycleOwner) {
                binding.layoutMonthSelector.txtMonth.text = it
            }

            datesList.observe(viewLifecycleOwner) {
                hideLoader(binding.loadingview.layoutLoading)
                if (it?.isEmpty() == false) {
                    hideError(binding.root)
                    binding.datesList.show()
                    adapterDatesRv.setDateListToRV(it.distinctBy { it.pst_start_date }, this@FixturesByDateFragment)
                } else {
                    binding.datesList.hide()
                    binding.matchesList.hide()
                    showErrorPage(ErrorType.NO_MATCH_FOUND)

                }

            }

            matchesList.observe(viewLifecycleOwner) {
                if (it?.matchesList?.isEmpty() == false) {
                    hideError(binding.root)
                    binding.matchesList.show()

                    if(selectedDate!=""){
                        adapterMatchesRv.updateFixturesListItems(mViewModel.getFilteredMatchesList(selectedDate)?.matchesList)
                    }else{
                        adapterMatchesRv.updateFixturesListItems(it.matchesList)
                    }

                } else {
                    binding.matchesList.hide()
                    showErrorPage(ErrorType.NO_MATCH_FOUND)
                }
            }

            posObserve.observe(viewLifecycleOwner) {
                when (it) {
                    0 -> {
                        binding.layoutMonthSelector.btnPrevious.isEnabled = false
                        binding.layoutMonthSelector.btnNext.requestFocus()
                    }
                    else -> {
                        binding.layoutMonthSelector.btnPrevious.isEnabled = true
                    }
                }
            }

            errorPageShow.observe(viewLifecycleOwner) {
                showErrorPage(it)
            }

        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FixturesBySeriesFragment.
         */
        @JvmStatic
        fun newInstance(keyListener: KeyListener?) =
            FixturesByDateFragment().apply {
                if (keyListener != null) {
                    this.keyListener = keyListener
                }
            }

    }

    override fun onItemClickListener(date: DateListModel) {
        Timber.d("DateRV Clicked Interafce:: " + date.pst_start_date)

        selectedDate = date.pst_start_date

        mViewModel.getMatchesList(selectedDate)
    }

    override fun onMatchesItemClickListener(match: FixturesByDate, position: Int) {
        Timber.d("MatchesRV Clicked Interafce:: " + match.title)
        mViewModel._targetUrl.value = match.getTargetUrlMatchInfo()

        clickedMatchCenterPos = position
    }

    override fun onFocusPositionChangeListener(position: Int) {
        binding.matchesList.smoothScrollToPosition(position)
        Timber.d("MatchesRV onFocusPositionChangeListener:: " + position)

    }

    override fun onFixturesWatchLiveItemClickListener(match: FixturesByDate) {

        var contentDetails= ArrayList<ContentDetail>()
        match.content_details?.forEach {
            val content_detail = ContentDetail(
                content_id = it.content_id ?: 0,
                name = it.name.toString(),
                priority = it.priority ?: 0,
                streaming_url = it.streaming_url.toString()
            )
            contentDetails.add(content_detail)
        }
        val playerBundleDataModel = Card(
            title = match.title,
            sub_title = match.sub_title,
            img_path = match.img_path,
            display_live_link = match.display_live_link,
            need_login = match.need_login,
            need_subscription = match.need_subscription,
            isExpandable = false,
            base_url_type = match.watch_live_base_url_type,
            target_url = match.watch_live_target_url,
            thumbnails = Thumbnails(
                match.thumbnails.mdb,
                match.thumbnails.mdb,
                match.thumbnails.mdb,
                match.thumbnails.mdb,
                match.thumbnails.mdb,
                match.thumbnails.mdb
            ),
            content_id = match.content_details?.get(0)?.content_id,
            event_id = match.event_id,
            content_type = match.content_type,
            content_details = contentDetails
        )

        redirectToPlayer(CardClickedEvent(playerBundleDataModel))
//        requireContext().startActivityWithData<PlaybackActivity>(playerBundleDataModel, null)
    }

    fun redirectToPlayer(event: CardClickedEvent) {
        val whereTo = CommonFunctions.whereToGo(event.card, requireContext())
        when(whereTo){
            GoTo.LOGIN ->{
                requireContext().startActivityWithOutData<LoginActivity>()
            }
            GoTo.PLAY_VIDEO ->{
                GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[2]
                requireContext().startActivityWithData<PlaybackActivity>(
                    event.card,
                    event.cardRow
                )
            }
            GoTo.SUBSCRIPTION ->{
                requireContext().startActivityWithData<SubscriptionActivity>(event.card)
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if(view?.id == R.id.buttonMatchCenter || view?.id == R.id.buttonWatchLive){
//                        selectMonthLayout()
                        selectDatesLayout()
                        return true
                    } else
//                        EventBus.getDefault().post(NavMenuToggleEvent(true))

                    keyListener.onKey(view, keyCode, event)
                }
                KeyEvent.KEYCODE_DPAD_RIGHT ->{

                    if(view?.id == R.id.itemView){
                        dateListPos = binding.datesList.getChildAdapterPosition(view)
                        binding.matchesList.scrollToPosition(0)

                        if(binding.matchesList.getChildAt(0)?.findViewById<MaterialButton>(R.id.buttonWatchLive)?.isVisible==true) {
                            binding.matchesList.getChildAt(0)
                                ?.findViewById<MaterialButton>(R.id.buttonWatchLive)?.requestFocus()
                        }else{
                            binding.matchesList.getChildAt(0)
                                ?.findViewById<MaterialButton>(R.id.buttonMatchCenter)?.requestFocus()
                        }
                    }
                }

            }
        }
        return false
    }

    private fun selectDatesLayout() {
         binding.datesList.getChildAt(dateListPos)?.requestFocus()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FixturesTabKeyDownEvent) {
        Timber.d("onMessageEvent FixturesTabKeyDownEvent:: " + view?.findFocus())

        binding.dummyButton.requestFocus()
        Timber.d("onMessageEvent FixturesTabKeyDownEvent1:: " + view?.findFocus())

//        if(mBinding.layoutMonthSelector.btnPrevious.isEnabled){
//            mBinding.layoutMonthSelector.btnPrevious.requestFocus()
//        }else{
//            mBinding.layoutMonthSelector.btnNext.requestFocus()
//        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MatchCenterDestroyed) {
        if(event.destroyed) {
            if (clickedMatchCenterPos != -1) {
                if(binding.matchesList.getChildAt(clickedMatchCenterPos)!=null){
                    binding.matchesList.getChildAt(clickedMatchCenterPos).requestFocus()
                }else {
                    val holder =
                        binding.matchesList.findViewHolderForAdapterPosition(clickedMatchCenterPos)
                    if (holder != null) {
                        holder.itemView.requestFocus()
                    }
                }
            }
        }

    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        showError(binding.root, errorType, errorMessage, backBtnListener = {activity?.onBackPressed()}, btnText = "Back")
    }

    fun selectMonthLayout(isPrevMonth: Boolean = false){
        if(isPrevMonth && binding.layoutMonthSelector.btnPrevious.isEnabled)
            binding.layoutMonthSelector.btnPrevious.requestFocus()
        else
            binding.layoutMonthSelector.btnNext.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

}