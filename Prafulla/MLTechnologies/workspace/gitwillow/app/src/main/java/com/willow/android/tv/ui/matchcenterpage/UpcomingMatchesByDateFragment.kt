package com.willow.android.tv.ui.matchcenterpage

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.willow.android.R
import com.willow.android.databinding.FragmentFixturesByDateBinding
import com.willow.android.databinding.FragmentFixturesBySeriesBinding
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.matchcenterpage.datamodel.Item
import com.willow.android.tv.ui.fixturespage.model.DateListModel
import com.willow.android.tv.ui.matchcenterpage.adapters.ItemClickListener
import com.willow.android.tv.ui.matchcenterpage.adapters.MatchesItemClickListener
import com.willow.android.tv.ui.matchcenterpage.adapters.UpcomingDatesAdapter
import com.willow.android.tv.ui.matchcenterpage.adapters.UpcomingMatchesListAdapter
import com.willow.android.tv.ui.matchcenterpage.viewmodel.MatchCenterViewModel
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.events.FixturesTabKeyDownEvent
import com.willow.android.tv.utils.events.MatchCenterDestroyed
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [UpcomingMatchesByDateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpcomingMatchesByDateFragment : BaseFragment(), ItemClickListener, MatchesItemClickListener,
    KeyListener {

    private var mViewModel: MatchCenterViewModel?=null
    private var mBinding: FragmentFixturesByDateBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!

    private var clickedMatchCenterPos: Int = -1

    private var keyListener: KeyListener? = null
    private val adapterDatesRv = UpcomingDatesAdapter(this)
    val adapterMatchesRv = UpcomingMatchesListAdapter(this, this)

    private var firstTimePosObserve: Boolean =true
    private var dateListPos: Int = 0

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

        mViewModel =
            ViewModelProviders.of(requireParentFragment())[MatchCenterViewModel::class.java]


        binding.apply {
//            layoutMonthSelector.btnNext.requestFocus()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutMonthSelector.btnNext.isFocusedByDefault = true
            }
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
                mViewModel?.nextButtonClicked()
            }
            layoutMonthSelector.btnPrevious.setOnClickListener {
                mViewModel?.previousButtonClicked()
            }

            layoutMonthSelector.btnNext.setOnKeyListener { v, keyCode, event ->
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (binding.datesList.visibility == View.GONE) {
                            return@setOnKeyListener true

                        } else {
                            keyListener?.onKey(v, keyCode, event)
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (binding.matchesList.visibility == View.GONE) {
                            layoutMonthSelector.btnNext.requestFocus()
                        } else {
                            makeFocusMatchListFirstItem()
                        }
                        return@setOnKeyListener true

                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        keyListener?.onKey(v, keyCode, event)
                        return@setOnKeyListener true
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (binding.layoutMonthSelector.btnPrevious.isEnabled)
                            binding.layoutMonthSelector.btnPrevious.requestFocus()
                        return@setOnKeyListener true
                    }

                }
                false
            }

            layoutMonthSelector.btnPrevious.setOnKeyListener { v, keyCode, event ->
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (binding.datesList.visibility == View.GONE) {
                            return@setOnKeyListener true
                        } else {
                            keyListener?.onKey(v, keyCode, event)
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        keyListener?.onKey(v, keyCode, event)
                        return@setOnKeyListener true
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        binding.layoutMonthSelector.btnNext.requestFocus()
                        return@setOnKeyListener true
                    }

                }
                false
            }
        }

        mViewModel?.apply {
            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        mViewModel?.getDatesList()
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
                    adapterDatesRv.setDateListToRV(it.distinctBy { it.pst_start_date },this@UpcomingMatchesByDateFragment)
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
                    adapterMatchesRv.setMatchesListToRV(it)
                } else {
                    binding.matchesList.hide()
                    showErrorPage(ErrorType.NO_MATCH_FOUND)

                }
            }

            posObserve.observe(viewLifecycleOwner) {
                when (it) {
                    0 -> {

                        binding.layoutMonthSelector.btnPrevious.isEnabled = false
                        if(!firstTimePosObserve) {
                            binding.layoutMonthSelector.btnNext.requestFocus()
                        }else{
                            firstTimePosObserve = false
                        }
                    }

                    else -> {
                        binding.layoutMonthSelector.btnPrevious.isEnabled = true
                    }
                }
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
            UpcomingMatchesByDateFragment().apply {
                if (keyListener != null) {
                    this.keyListener = keyListener
                }
            }

    }

    override fun onItemClickListener(date: DateListModel) {
        Timber.d("DateRV Clicked Interafce:: " + date.pst_start_date)

        mViewModel?.getMatchesList(date.pst_start_date)
    }

    override fun onMatchesItemClickListener(match: Item, position: Int) {
        Timber.d("MatchesRV Clicked Interafce:: " + match.title)
        mViewModel?._targetUrl?.value = match.getTargetUrlMatchInfo()
        clickedMatchCenterPos = position

    }

    /* override fun onFixturesWatchLiveItemClickListener(match: Item) {

         val playerBundleDataModel =  Card(
             title = match.title,
             sub_title = match.sub_title,
             img_path = match.img_path,
             display_live_link = match.display_live_link,
             need_subscription = match.need_subscription,
             isExpandable = false,
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
             content_type = match.content_type

         )

         requireContext().startActivityWithData<PlaybackActivity>(playerBundleDataModel,null)
     }*/
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

                    keyListener?.onKey(view, keyCode, event)
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    /* If date list has focus has user press right button
                    * Focus will move to upcoming match list item*/
                    if (view?.id == R.id.itemView) {
                        dateListPos = binding.datesList.getChildAdapterPosition(view)
                        binding.matchesList.getChildAt(0).requestFocus()
                    }
                }


                KeyEvent.KEYCODE_DPAD_UP -> {
                    keyListener?.onKey(view, keyCode, event)
                    return true
                }

            }
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FixturesTabKeyDownEvent) {
        Timber.d("onMessageEvent FixturesTabKeyDownEvent:: " + view?.findFocus())

        binding.dummyButton.requestFocus()
        Timber.d("onMessageEvent FixturesTabKeyDownEvent1:: " + view?.findFocus())

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
        showError(binding.root, errorType, errorMessage)
    }

    override fun focusItem() {
        selectMonthLayout(true)
    }

    private fun selectMonthLayout(isPrevMonth: Boolean = false) {
        if (isPrevMonth && binding.layoutMonthSelector.btnPrevious.isEnabled)
            binding.layoutMonthSelector.btnPrevious.requestFocus()
        else
            binding.layoutMonthSelector.btnNext.requestFocus()
    }

    private fun selectDatesLayout() {
        binding.datesList.getChildAt(dateListPos).requestFocus()
    }


    private fun makeFocusMatchListFirstItem() {
        if (binding.matchesList.childCount > 0) {
            val view = binding.matchesList.getChildAt(0)
            view?.let { it ->
                val watchLiveButton = it.findViewById<AppCompatButton>(R.id.buttonMatchCenter)
                watchLiveButton?.let { button ->
                    button.requestFocus()
                    button.isSelected = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mViewModel = null
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
}