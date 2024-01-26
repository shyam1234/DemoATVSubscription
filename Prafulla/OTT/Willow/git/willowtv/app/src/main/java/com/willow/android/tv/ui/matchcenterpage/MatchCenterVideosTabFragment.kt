package com.willow.android.tv.ui.matchcenterpage

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.BaseOnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.ViewModelProviders
import com.willow.android.R
import com.willow.android.databinding.FragmentMatchCenterVideosTabBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.CardListRow
import com.willow.android.tv.common.cards.CardRowsContainerFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.ui.matchcenterpage.model.MatchCenterPageModel
import com.willow.android.tv.ui.matchcenterpage.viewmodel.MatchCenterViewModel
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.events.KeyPressedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

/**
 * A Tab [Fragment] for listing videos.
 * Use the [MatchCenterVideosTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MatchCenterVideosTabFragment : BaseFragment(),
    BaseOnItemViewSelectedListener<CardListRow>, KeyListener {

    private var mViewModel: MatchCenterViewModel?=null
    private var indexOfItem: Int = -1

    private var mBinding: FragmentMatchCenterVideosTabBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = mBinding!!
    private var cardRowSupportFragment: CardRowsContainerFragment? = null
    private var keyListener: KeyListener? = null
    private var cardRowModel: ArrayList<CardRow>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMatchCenterVideosTabBinding.inflate(
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

        mViewModel?.apply {
            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> onSuccess()
                    else -> {}
                }
            }
            cardRowsList.observe(viewLifecycleOwner) { matchCenterPageModel ->
                if (matchCenterPageModel != null) {
                    binding.apply {
                        txtDate.text = matchCenterPageModel.date.toString()
                        txtMatchTitle.text = matchCenterPageModel.title
                    }
                    if(matchCenterPageModel.cardRowModel.none { (it.items?.size ?: 0) > 0 })
                        showErrorPage()
                    else
                        renderPage(matchCenterPageModel)
                } else
                    showErrorPage()
            }
        }
    }

    fun onSuccess(){
        if(mViewModel?.cardRowsList?.value ==null) {
            CoroutineScope(Dispatchers.IO).launch {
                mViewModel?.getVideosRows()
            }
        }
    }

    private fun renderPage(data: MatchCenterPageModel) {
        initCardRowsContainer(data.cardRowModel)
        Timber.d("called renderPage")
    }


    private fun initCardRowsContainer(cardRowModel: ArrayList<CardRow>) {
        this.cardRowModel = cardRowModel
        cardRowSupportFragment = CardRowsContainerFragment.newInstance(this, cardRowModel, this,false)
        cardRowSupportFragment?.let {
            val transaction: FragmentTransaction? = childFragmentManager.beginTransaction()
            transaction?.replace(
                R.id.swimlane_container,
                it
            )
            transaction?.addToBackStack(null)
            if(!childFragmentManager.isStateSaved) {
                transaction?.commit()
            }else{
                transaction?.commitAllowingStateLoss()
            }
        }
    }


    /**
     * this callback gives selected card row and card details
     */
    override fun onItemSelected(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: CardListRow?
    ) {
        val card = item as? Card
        indexOfItem = ((row as CardListRow).adapter as ArrayObjectAdapter).indexOf(item)
//        Timber.d("indexOfItem :: $indexOfItem ")
        //-----------------------------------------
        row.let {
            when (row.getCardRow().getCardType()) {
                Types.Card.PORTRAIT_TO_LANDSCAPE -> {
//                    removeHeroBanner()
                }
                Types.Card.BILLBOARD,
                Types.Card.LARGE_PORTRAIT,
                Types.Card.LEADERBOARD,
                Types.Card.MEDIUM_LANDSCAPE,
                Types.Card.LARGE_LANDSCAPE,
                Types.Card.SMALL_LANDSCAPE -> {
//                    if (card != null) {
//                        updateHeroBannerOnCardSelection(card)
//                    }
                }
                else -> {
                    //for unknown card, do nothing
                }
            }
        }

    }


    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    cardRowSupportFragment?.let {
                        if (it.selectedPosition == 0) {
//                            view?.tag = THUMBNAIL_TAG
                            keyListener?.onKey(view, keyCode, event)
                            return true
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    cardRowSupportFragment?.let {
                        if (it.selectedPosition == it.adapter.size() - 1) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    companion object {
        const val THUMBNAIL_TAG = "thumbnail"
        @JvmStatic
        fun newInstance(keyListener: KeyListener? = null) =
            MatchCenterVideosTabFragment().apply {
                this.keyListener = keyListener
            }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: KeyPressedEvent) {
        Timber.d("Focussed View:: " + view?.findFocus())
        binding.swimlaneContainer.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        Timber.d("***** onStart() ")
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        Timber.d("***** onStop() ")
        super.onStop()
    }

    private fun showErrorPage() {
        showError(binding.root, ErrorType.NO_VIDEO_FOUND)
    }

    override fun focusItem() {
        cardRowSupportFragment?.let {
            it.view?.requestFocus()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
        mViewModel = null
        cardRowSupportFragment = null
        keyListener = null
        cardRowModel = null
    }
}