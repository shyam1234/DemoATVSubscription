package com.willow.android.tv.common.carousel


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.willow.android.R
import com.willow.android.databinding.RowCarouselVideoItemBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.common.navmenu.NavigationMenuCallback
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.CommonFunctions.calculateProgressPercent
import com.willow.android.tv.utils.CommonFunctions.getRemainingTime
import com.willow.android.tv.utils.events.CardClickedEvent
import com.willow.android.tv.utils.extension.text
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class CarouselRowItemFragment : Fragment(), View.OnClickListener,
    View.OnFocusChangeListener, View.OnKeyListener {

    private var currentPosition: Int = 0
    private var mBinding: RowCarouselVideoItemBinding? = null
    private var type: Types.HeroBanner? = null
    private var data: Card? = null
    private var isContinueWatching: Boolean? = null
    private var navigationMenuCallback: NavigationMenuCallback? = null
    private var keyListener: KeyListener? = null

    companion object {
        @JvmStatic
        fun newInstance(
            type: Types.HeroBanner?,
            data: Card?,
            index: Int,
            isContinueWatching: Boolean?,
            navigationMenuCallback: NavigationMenuCallback?,
            keyListener: KeyListener?
        ) =
            CarouselRowItemFragment().apply {
                this.type = type
                this.data = data
                this.currentPosition = index
                this.isContinueWatching = isContinueWatching
                this.navigationMenuCallback = navigationMenuCallback
                this.keyListener = keyListener
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): RelativeLayout? {
        mBinding = RowCarouselVideoItemBinding.inflate(inflater.cloneInContext(context), container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingData()
    }

    private fun bindingData() {
        setAllInvisible()

        //For title
        //val html = getRichText("<b>${data?.team_one_name}</b><br>vs<br><b>${data?.team_two_name}</b>")
        val html = data?.getTeamsWithTheirScores()
        if (html?.isEmpty() == true) {
            mBinding?.carouselWithText?.textviewCarouselTitle?.text(getRichText("<b>${data?.title}</b>"))
        } else {
            mBinding?.carouselWithText?.textviewCarouselTitle?.text(html)
        }
        //For subtitle
        mBinding?.carouselWithText?.textviewCarouselSubtitle?.text(data?.sub_title)

        //For last line description
        mBinding?.carouselWithText?.textviewCarouselContent?.text(data?.description)
        //For venue
        mBinding?.carouselWithText?.textviewStadium?.text(data?.ground_details)

        //For chips
        data?.tags?.forEach { text ->
            val textView = TextView(context)
            textView.text = text
            textView.setBackgroundResource(R.color.color_chip_bg)
            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(context, R.style.ChipTextAppearance)
            } else {
                textView.setTextAppearance(R.style.ChipTextAppearance)
            }
            textView.setPadding(4, 4, 4, 4)
            mBinding?.carouselWithText?.chipGroupCarouselMain?.visibility = View.VISIBLE
            mBinding?.carouselWithText?.chipGroupCarouselMain?.addView(textView)
            //for adding gap
            val textView1 = TextView(context)
            textView1.text = " "
            textView1.setBackgroundResource(R.color.transparent)
            mBinding?.carouselWithText?.chipGroupCarouselMain?.addView(textView1)
        }

        //For Continue Watching Progressbar
        if ((data?.progress ?: 0.0) > 0.0 && isContinueWatching == true) {
            mBinding?.carouselWithText?.llCarouselProgressHolder?.show()
            mBinding?.carouselWithText?.progressBar?.progress = calculateProgressPercent(
                data?.progress ?: 0.0, data?.duration_seconds?.toDouble() ?: 0.0
            )
            mBinding?.carouselWithText?.progressbarTimeRemaining?.text = getRemainingTime(
                data?.progress ?: 0.0, data?.duration_seconds?.toDouble() ?: 0.0
            )
        }

        //For subscription icon, Live icon, watch and preview button
        if (data?.isShowPrimeTag() == true) {
            mBinding?.carouselWithText?.imageviewPrimeTag?.visibility = View.VISIBLE
        }

        //enable_button is there to goto a detail page or a video, based on target_url and target_action
        if (data?.isPoster == false && data?.enable_button == true) {
            if (!data?.button_title.isNullOrEmpty()) {
                mBinding?.carouselWithText?.buttonCarouselWatch?.text = data?.button_title
                mBinding?.carouselWithText?.buttonCarouselWatch?.visibility = View.VISIBLE
            }
        }
        //show live tag
        when (data?.content_type?.uppercase()) {
            Types.Content.LIVE.name -> {
                if (data?.isShowLiveTag() == true) {
                    mBinding?.carouselWithText?.imageviewLiveTag?.visibility = View.VISIBLE
                }
            }

            Types.Content.TEAM.name -> {
                mBinding?.carouselWithText?.textviewCarouselSubtitle?.visibility = View.GONE
                mBinding?.carouselWithText?.textviewStadium?.visibility = View.GONE
                mBinding?.carouselWithText?.chipGroupCarouselMain?.visibility = View.GONE
                mBinding?.carouselWithText?.llCarouselProgressHolder?.hide()
                mBinding?.carouselWithText?.textviewCarouselTitle?.text(data?.team_name ?: "")
                mBinding?.carouselWithText?.textviewCarouselContent?.text(data?.description)
            }

            Types.Content.MATCH.name,
            Types.Content.UPCOMING_MATCH.name -> {
                mBinding?.carouselWithText?.textviewEvent?.text =
                    CommonFunctions.convertTimeToDigitalFormat(data?.time)
                if (!mBinding?.carouselWithText?.textviewEvent?.text.isNullOrEmpty()) {
                    mBinding?.carouselWithText?.textviewEvent?.visibility = View.VISIBLE
                }
            }

            Types.Content.HIGHLIGHT.name -> {

            }

            Types.Content.AD.name -> {

            }

            Types.Content.SERIES.name -> {

            }

            Types.Content.MATCH.name -> {

            }

            Types.Content.CLIP.name -> {

            }

            Types.Content.REPLAY.name -> {

            }
        }

        var url: String? = data?.getPosterHRB()
        if (data?.isPoster == false) {
            url = data?.getCarouselBG()
        }
        Timber.d("carousel poster url++ $url")
        if (TextUtils.isEmpty(url)) {
            url = null
        }

        mBinding?.carouselWithText?.buttonCarouselWatch?.setOnClickListener(this)
        mBinding?.carouselWithText?.buttonCarouselWatch?.onFocusChangeListener = this
        mBinding?.carouselWithText?.buttonCarouselWatch?.setOnKeyListener(this)
        mBinding?.carouselWithText?.buttonToHandleFocus?.setOnKeyListener(this)
    }


    fun onFocused() {
    }

    private fun getRichText(str: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(str)
        }
    }

    private fun setAllInvisible() {
        mBinding?.carouselWithText?.imageviewLiveTag?.visibility = View.GONE
        mBinding?.carouselWithText?.textviewEvent?.visibility = View.GONE
        mBinding?.carouselWithText?.imageviewPrimeTag?.visibility = View.GONE
        mBinding?.carouselWithText?.chipGroupCarouselMain?.visibility = View.GONE
        mBinding?.carouselWithText?.buttonCarouselWatch?.visibility = View.GONE
        mBinding?.carouselWithText?.llCarouselProgressHolder?.visibility = View.GONE
        mBinding?.carouselWithText?.root?.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            mBinding?.carouselWithText?.buttonCarouselWatch?.id -> {
                data?.let {
                    EventBus.getDefault().post(CardClickedEvent(it))
                }
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            mBinding?.carouselWithText?.buttonCarouselWatch?.id -> {
                if (hasFocus) {
                    requireContext().let {
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setBackgroundColor(
                            ContextCompat.getColor(it, R.color.carousel_btn_selected_color)
                        )
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setTextColor(
                            ContextCompat.getColor(
                                it,
                                R.color.carousel_btn_selected_text_color
                            )
                        )
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.selected_play_btn,
                            0,
                            0,
                            0
                        )
                    }
                } else {
                    requireContext().let {
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setBackgroundColor(
                            ContextCompat.getColor(it, R.color.carousel_btn_default_color)
                        )
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setTextColor(
                            ContextCompat.getColor(
                                it,
                                R.color.carousel_btn_defult_text_color
                            )
                        )
                        mBinding?.carouselWithText?.buttonCarouselWatch?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.play_btn,
                            0,
                            0,
                            0
                        )
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (currentPosition > 0) {
                        return false
                    }
                    navigationMenuCallback?.navMenuToggle(true)
                    return true
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    keyListener?.onKey(view, keyCode, event)
                    return true
                }
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}