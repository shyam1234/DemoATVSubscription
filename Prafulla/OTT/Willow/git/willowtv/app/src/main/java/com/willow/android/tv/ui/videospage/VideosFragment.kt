package com.willow.android.tv.ui.videospage

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.FragmentTransaction
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.BaseOnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.RowPresenter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentExploreBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.CardListRow
import com.willow.android.tv.common.cards.CardRowsContainerFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.common.carousel.CarouselFragment
import com.willow.android.tv.common.carousel.models.HeroBanner
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.data.repositories.commondatamodel.CommonCardRow
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.explorepage.model.ExplorePageModel
import com.willow.android.tv.ui.videospage.viewmodel.VideosViewModel
import com.willow.android.tv.ui.videospage.viewmodel.VideosViewModelFactory
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.GlobalConstants.DELAY_IN_RENDERING_POSTER
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.Utils
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class VideosFragment() : BaseFragment(),
    BaseOnItemViewSelectedListener<CardListRow>, KeyListener {
    private var url: String? = null
    private var indexOfItem: Int = -1
    private lateinit var videosPageModel: ExplorePageModel
    private var carouselFragment: CarouselFragment? = null
    private lateinit var mViewModel: VideosViewModel
    private var mBinding: FragmentExploreBinding? = null
    private var cardRowSupportFragment: CardRowsContainerFragment? = null
    private var carousalHeroBanner: HeroBanner? = null
    private var isCarousalHasFocus = true
    private var isThisTeamPage: Boolean = false
    private var runnableHeroBanner: Runnable? = null

    init {
        Timber.d("called VideosFragment")
    }

    companion object {
        @JvmStatic
        fun newInstance(isThisTeamPage:Boolean = false , data: NavigationTabsDataModel? = null, url: String? = null ) = VideosFragment().apply {
           this.isThisTeamPage =  isThisTeamPage
            arguments = Bundle().apply {
                putString(GlobalConstants.Keys.URL, url)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        url = arguments?.getString(GlobalConstants.Keys.URL, null)
        mBinding = FragmentExploreBinding.inflate(inflater.cloneInContext(context), container, false)
//        if(isThisTeamPage) {
//            mBinding.mainHolder.setPaddingRelative(0, 0, 0, 0)
//        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationMenuCallback?.navMenuScreenName(Types.ScreenName.VIDEOS)
        mViewModel = ViewModelProvider(this, VideosViewModelFactory(WillowApplication.instance))[(VideosViewModel::class.java)]
        mViewModel.loadVideosPageConfig(url)
        mViewModel.renderPage.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> onSuccess(it.data)
                is Resource.Error -> {
                    mBinding?.loadingview?.layoutLoading?.hide()
                    showErrorPage(ErrorType.NO_DATA_FOUND)
                    navigationMenuCallback?.navMenuToggle(true)
                }
                is Resource.Loading -> onLoading()
            }

        }
    }

    private fun onSuccess(data: CommonCardRow?) {
        mBinding?.loadingview?.layoutLoading?.hide()
        val cardRows: ArrayList<CardRow>? = data?.result?.rows
        var heroBanner: HeroBanner? = null
        cardRows?.let { row ->
            row.forEach {
                if (it.getCardType() == Types.Card.CAROUSEL) {
                    heroBanner = HeroBanner(it.items, Types.HeroBanner.CAROUSEL)
                }
            }
            videosPageModel = ExplorePageModel(heroBanner, row)
            renderPage(videosPageModel)
        }
    }

    private fun onLoading() {

        mBinding?.loadingview?.layoutLoading?.show()
        val imageViewAnimator =
            ObjectAnimator.ofFloat(mBinding?.loadingview?.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    private fun renderPage(data: ExplorePageModel) {
        carousalHeroBanner = data.getCarouselData()
        initCardRowsContainer(data.cardRowModel)
        lifecycleScope.launch {
            if(isCarousalVisible()) {
                delay(3000)
                initHeroBanner(carousalHeroBanner, 0, true)
            }
        }

        Timber.d("called renderPage")
    }


    private fun initHeroBanner(
        heroBannerModel: HeroBanner?,
        delay: Long,
        isFocus: Boolean = false
    ) {
        if (runnableHeroBanner != null) {
            mBinding?.herobanner?.removeCallbacks(runnableHeroBanner)
            runnableHeroBanner = null
        }
        runnableHeroBanner = Runnable {
            carouselFragment = CarouselFragment.newInstance(heroBannerModel,navigationMenuCallback,this)
            carouselFragment?.let {fragment->
                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                transaction.replace(R.id.herobanner, fragment)
                if (!childFragmentManager.isStateSaved) {
                    transaction.commit()
                }else{
                    transaction.commitAllowingStateLoss()
                }
                lifecycleScope.launch {
                    delay(500)
                    if (isFocus)
                        carouselFragment?.makeFocusItem()
                }
            }
        }
        mBinding?.herobanner?.postDelayed(runnableHeroBanner, delay)
    }

    private fun initCardRowsContainer(cardRowModel: ArrayList<CardRow>?) {
        cardRowModel?.let {
            mBinding?.swimlaneContainer?.post {
                cardRowSupportFragment = CardRowsContainerFragment.newInstance(this, it, this)
                mBinding?.swimlaneContainer?.visibility = View.VISIBLE
                val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                transaction.replace(R.id.swimlane_container, cardRowSupportFragment!!)
                if (!childFragmentManager.isStateSaved) {
                    transaction.commit()
                }else{
                    transaction.commitAllowingStateLoss()
                }
            }
        }
    }

    private fun removeHeroBanner() {
        mBinding?.herobanner?.visibility = View.GONE
    }

    private fun updateHeroBannerOnCardSelection(card: Card?) {
        card?.let {
            val listItems = ArrayList<Card>()
            listItems.add(it)
            val model = ExplorePageModel(HeroBanner(listItems, Types.HeroBanner.CAROUSEL), ArrayList())
            initHeroBanner(model.getCarouselData(),DELAY_IN_RENDERING_POSTER)
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
        card?.isPoster = true
        indexOfItem = ((row as CardListRow).adapter as ArrayObjectAdapter).indexOf(item)
        Timber.d("indexOfItem :: $indexOfItem ")
        //-----------------------------------------
        row.let {
            when (row.getCardRow().getCardType()) {
                Types.Card.PORTRAIT_TO_LANDSCAPE -> {
                    removeHeroBanner()
                }
                Types.Card.BILLBOARD,
                Types.Card.LARGE_PORTRAIT,
                Types.Card.LEADERBOARD,
                Types.Card.MEDIUM_LANDSCAPE,
                Types.Card.LARGE_LANDSCAPE,
                Types.Card.SMALL_LANDSCAPE -> {
                    if (card != null) {
                        mBinding?.herobanner?.visibility = View.VISIBLE
                        updateHeroBannerOnCardSelection(card)
                    }
                }
                else -> {
                    //for unknown card, do nothing
                }
            }
        }

    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        Utils.memoryLogs()
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (indexOfItem == 0) {
                        navigationMenuCallback?.navMenuToggle(true)
                        isCarousalHasFocus = carouselFragment?.view?.hasFocus() == true
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if(view?.id == R.id.button_carousel_watch || view?.id == R.id.button_to_handle_focus) {
                        updateHeroBannerOnCardSelection(videosPageModel.cardRowModel?.get(1)?.items?.get(0))
                        cardRowSupportFragment?.focusFirstItem()
                        return true

                    }else {
                        cardRowSupportFragment?.let { fragment ->
                            if (fragment.selectedPosition == fragment.adapter.size() - 1)
                                return true
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    cardRowSupportFragment?.let {
                        if (it.view?.hasFocus() == true && it.selectedPosition == 0) {
                            if(isCarousalVisible()) {
                                initHeroBanner(carousalHeroBanner, 0)
                                carouselFragment?.makeFocusItem()
                            }
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        showError(mBinding?.root, errorType, errorMessage, backBtnListener = {activity?.onBackPressed()}, btnText = "Back")
    }



    override fun focusItem() {
        lifecycleScope.launch {
            delay(200)
            cardRowSupportFragment?.view?.requestFocus()
        }

    }

    private fun isCarousalVisible(): Boolean {
        carousalHeroBanner?.listItems?.let {
            return it.isNotEmpty()
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        GlobalTVConfig.currentPage = resources.getStringArray(R.array.arrayPages)[1]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}