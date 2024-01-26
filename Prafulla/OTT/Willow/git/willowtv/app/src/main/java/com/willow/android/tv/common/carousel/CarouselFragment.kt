package com.willow.android.tv.common.carousel

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.willow.android.R
import com.willow.android.databinding.FragmentCarouselBinding
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.common.carousel.adapters.CarouselViewPagerAdapter
import com.willow.android.tv.common.carousel.models.HeroBanner
import com.willow.android.tv.common.navmenu.NavigationMenuCallback
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.ui.playback.IPlayerStatus
import com.willow.android.tv.ui.playback.PlayerManager
import com.willow.android.tv.utils.DotsScrollBar
import com.willow.android.tv.utils.GlobalConstants
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.Utils
import kotlinx.coroutines.launch
import timber.log.Timber

class CarouselFragment() : Fragment(),
    ViewPager.OnPageChangeListener {
    private var heroBannerModel: HeroBanner? = null
    private var navigationMenuCallback: NavigationMenuCallback? = null
    private var keyListener: KeyListener? = null
    private var mBinding: FragmentCarouselBinding?= null
    private var runnableAutoScroll: Runnable? = null
    private var handlerAutoScroll: Handler? = null
    private var playerManager:PlayerManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            heroBannerModel: HeroBanner?,
            navigationMenuCallback: NavigationMenuCallback?,
            keyListener: KeyListener
        ) = CarouselFragment().apply {
            this.heroBannerModel = heroBannerModel
            this.navigationMenuCallback = navigationMenuCallback
            this.keyListener = keyListener
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve the object from the arguments bundle
        mBinding = FragmentCarouselBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager(view)
    }

    private fun initViewPager(view: View) {
        mBinding?.let { mBinding->
            mBinding.viewPagerCarousel.adapter = childFragmentManager?.let {
                CarouselViewPagerAdapter(
                    it,
                    FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                    heroBannerModel,
                    navigationMenuCallback,
                    keyListener
                )
            }
            mBinding.viewPagerCarousel.offscreenPageLimit = 1
            mBinding.viewPagerCarousel.setKeyEventsEnabled(true)
            mBinding.viewPagerCarousel.addOnPageChangeListener(this)
            mBinding.linIndicatorHolder.visibility = View.GONE
            if((heroBannerModel?.listItems?.size ?: 0) > 1){
                mBinding.linIndicatorHolder.visibility = View.VISIBLE
                setAutoScroll()
            }
            onPageSelected(0)
        }
    }



    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        Utils.memoryLogs()
        // Set the currentPage variable to the currently focused item
        showPosterAndPlayTrailer(position)
        mBinding?.let {
            val size =  heroBannerModel?.listItems?.size?:0
            if(size > 1) {
                onNext(GlobalConstants.DELAY_IN_AUTO_SCROLL_CAROUSEL_WITH_POSTER)
                updateIndicator(it.linIndicatorHolder, position, size)
            }
        }

    }

    private fun showPosterAndPlayTrailer(position: Int) {
        Utils.memoryLogs()
        val data = heroBannerModel?.listItems?.get(position)
        ImageUtility.loadImage(data?.getPosterHRB(), R.drawable.default_herobanner,mBinding?.imageviewCarouselBg)
        bindPlayer(data)
    }


    private fun bindPlayer(data: Card?) {
        playerManager?.stopContent()
        if((data?.play_trailer == true || data?.enable_trailer == true) && !TextUtils.isEmpty(data?.trailer)){
            playerManager = PlayerManager()
            playerManager?.initPlayer( context, mBinding?.carouselWithPlayerView, data?.trailer.toString(), object :
                IPlayerStatus {
                override fun onPrepare() {
                    mBinding?.carouselWithPlayerView?.visibility = View.GONE
                    mBinding?.imageviewCarouselBg?.visibility = View.VISIBLE
                }
                override fun onPlay() {
                    playerManager?.getContentDuration()?.let {
                        val time = if(it <= 0 || it > 1000000 ) GlobalConstants.DELAY_IN_AUTO_SCROLL_CAROUSEL_WITH_LIVE_TRAILER else it
                        onNext(time)
                    }
                    //use carouselWithText to hide the text over the playback
                    mBinding?.carouselWithPlayerView?.visibility = View.VISIBLE
                    mBinding?.imageviewCarouselBg?.visibility = View.GONE
                }

                override fun onEnd() {
                    mBinding?.carouselWithPlayerView?.visibility = View.GONE
                    mBinding?.imageviewCarouselBg?.visibility = View.VISIBLE

                }
            })
            playerManager?.playContent()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE -> {
                mBinding?.viewPagerCarousel?.alpha = 1.0f
                // The pager is in idle state (not scrolling)
                // Perform any necessary actions here
            }
            ViewPager.SCROLL_STATE_DRAGGING -> {
                mBinding?.viewPagerCarousel?.alpha = 0.0f
                // The pager is being dragged by the user
                // Perform any necessary actions here
            }
            ViewPager.SCROLL_STATE_SETTLING -> {
                mBinding?.viewPagerCarousel?.alpha = 0.0f
                // The pager is in the process of settling after a user swipe
                // Perform any necessary actions here
            }
        }
    }

    private fun updateIndicator(dots_scrollbar_holder : LinearLayout, mCurrentPage: Int, totalNumberOfPages: Int ) {
        context?.let {
            dots_scrollbar_holder.removeAllViews()
            DotsScrollBar.createDotScrollBar(it, dots_scrollbar_holder, mCurrentPage, totalNumberOfPages)
        }
    }


    fun makeFocusItem(){
        lifecycleScope.launch {
            mBinding?.viewPagerCarousel?.requestFocus()
            mBinding?.viewPagerCarousel?.isSelected = true
        }
    }

    fun onNext(delay: Long) {
        runnableAutoScroll?.let{
            handlerAutoScroll?.removeCallbacks(it)
            handlerAutoScroll?.postDelayed(it, delay)
        }
    }

    private fun setAutoScroll() {
        handlerAutoScroll = Handler(Looper.myLooper()!!)
        runnableAutoScroll = Runnable {
            mBinding?.let {
                val currentItem =  it.viewPagerCarousel.currentItem
                val totalItems =  it.viewPagerCarousel.adapter?.count ?: 0
                val nextItem = (currentItem + 1) % totalItems
                it.viewPagerCarousel.setCurrentItem(nextItem, true)
            }

        }
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView")
        super.onDestroyView()
        mBinding = null
        playerManager = null
        runnableAutoScroll?.let {
            handlerAutoScroll?.removeCallbacks(it)
        }
        handlerAutoScroll = null
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
        playerManager?.stopContent()
        runnableAutoScroll?.let {
            handlerAutoScroll?.removeCallbacks(it)
        }
    }
}
