package com.willow.android.tv.common.cards.presenters

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.bitmovin.player.PlayerView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.willow.android.R
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import com.willow.android.tv.ui.playback.IPlayerStatus
import com.willow.android.tv.ui.playback.PlayerManager
import com.willow.android.tv.utils.ImageUtility
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.events.CardClickedEvent
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

open class DefaultCardViewPresenter(
    private val listener: KeyListener,
    private val cardRow: CardRow
) : Presenter(), View.OnFocusChangeListener, View.OnClickListener, View.OnKeyListener {

    var url: String? = ""
    var width: Int? = 0
    var height: Int? = 0
    var marginStart: Int = 0
    var marginEnd: Int = 0
    var marginTop: Int = 0
    var marginBottom: Int = 0
    var context: Context? = null
    var isExpandableCard: Boolean = false
    var isExpandedCard: Boolean = false
    var imagePlaceHolder: Int = R.drawable.default_medium_holder

    private lateinit var prefRepository: PrefRepository


    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder? {
        context = parent?.context
        val view: View
        when (cardRow.getItemCategory()) {
            Types.CardRowCategory.MATCHES -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_match_card_item, parent, false)
            }

            Types.CardRowCategory.VIDEO -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_video_card_item, parent, false)
            }

            Types.CardRowCategory.TEAMS_LIST -> {
                view =
                    LayoutInflater.from(context).inflate(R.layout.row_team_card_item, parent, false)
            }

            Types.CardRowCategory.SERIES -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_series_card_item, parent, false)
            }

            Types.CardRowCategory.UPCOMING -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_upcoming_card_item, parent, false)
            }

            else -> {
                view = LayoutInflater.from(context)
                    .inflate(R.layout.row_video_card_item, parent, false)
            }
        }

        prefRepository = PrefRepository(context)


        val holder = CardViewHolder(cardRow.getItemCategory(), view)
        holder.cardview.isFocusableInTouchMode = true
        holder.cardview.onFocusChangeListener = this
        holder.playerView.onFocusChangeListener = this
        holder.cardview.isFocusable = true
        return holder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        val holder = viewHolder as CardViewHolder
        val data = item as Card
        if (data.isExpandable) {
            bindPlayer(holder, data)
            if (!isExpandedCard) {
                val resources = context?.resources
                width = resources?.getDimensionPixelSize(R.dimen.presenter_card_medium_portrait_expandable_after_width)
                url = data.getThumbnailHRB()
                imagePlaceHolder = R.drawable.default_herobanner
            } else {
                holder.expandableTextHolder.visibility = View.VISIBLE
            }
        }


        val param: FrameLayout.LayoutParams = FrameLayout.LayoutParams(width!!, height!!)
        param.setMargins(marginStart, marginTop, marginEnd, marginBottom)
        holder.cardContainer.layoutParams = param

        holder.primeTag.visibility = View.GONE
        if (item.isShowPrimeTag()) {
            holder.primeTag.visibility = View.VISIBLE
        }

        holder.cardview.tag = CardInfo(viewHolder, data)
        Timber.d("TAG", "onBindViewHolder: " + holder.cardview.tag)
        holder.cardview.setOnClickListener(this)
        holder.cardview.setOnKeyListener(this)
        updateCardHighlighter(holder.cardview, data.isExpandable)
        updateCardHighlighter(holder.playerView, data.isExpandable)

        //For rendering image---------------------------------
        if (TextUtils.isEmpty(url)) {
            url = null
        }
        ImageUtility.loadImagewithRoundCornersTransformWithCallBack(
            url,
            imagePlaceHolder,
            holder.cardview,
            callback(holder, data)
        )
        Timber.d(">> card thumbnailURL: $url")

    }


    private fun callback(
        holder: CardViewHolder,
        data: Card
    ) = object : CustomTarget<Drawable>() {

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            holder.cardview.setImageDrawable(resource)
            if (isExpandableCard || isExpandedCard) {
                handleExpandableCardTextVisibility(holder, data, true)
            }
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            holder.cardview.setImageDrawable(placeholder)
            // handleExpandableCardTextVisibility(holder, data)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            holder.cardview.setImageDrawable(errorDrawable)
            if (isExpandableCard || isExpandedCard) {
                handleExpandableCardTextVisibility(holder, data, false)
            }

        }

        override fun onLoadStarted(placeholder: Drawable?) {
            super.onLoadStarted(placeholder)
            holder.cardview.setImageDrawable(placeholder)
        }
    }

    private fun bindPlayer(holder: CardViewHolder, item: Card) {
        holder.playerManager = PlayerManager()
        holder.playerManager?.initPlayer(context, holder.playerView, "", object : IPlayerStatus {
                override fun onPrepare() {
                    holder.playerView.visibility = View.GONE
                }

                override fun onPlay() {
                    holder.playerView.visibility = View.VISIBLE
                }

                override fun onEnd() {
                    holder.playerView.visibility = View.GONE
                }
            })
        holder.playerManager?.setURL(item.trailer)
    }

    private fun handleExpandableCardTextVisibility(
        holder: CardViewHolder,
        data: Card,
        isTextShow: Boolean
    ) {
        if (data.isExpandable) {
            if (data.play_trailer == true || data.enable_trailer == true) {
                holder.playerManager?.playContent()
            }
            if(isTextShow) {
                holder.expandableTextHolder.visibility = View.VISIBLE
            }
        } else {
            holder.playerManager?.stopContent()
            if (!isExpandedCard) {
                holder.expandableTextHolder.visibility = View.GONE
            }
        }

    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.cardview -> {
                // val cardInfo = v.tag as CardInfo
                // cardInfo.getCard().isFocused = hasFocus
                updateCardHighlighter(v.findViewById(R.id.cardview) as ImageView, hasFocus)
            }
            R.id.player_view -> {
                // val cardInfo = v.tag as CardInfo
                // cardInfo.getCard().isFocused = hasFocus
                updateCardHighlighter(v.findViewById(R.id.player_view) as PlayerView, hasFocus)
            }
        }
    }

    private fun updateCardHighlighter(
        imageView: View?,
        selected: Boolean
    ) {
        val selectedColorID = context?.let {
            ContextCompat.getColor(
                it,
                R.color.card_background_selected
            )
        }
        val unselectedColorID = context?.let {
            ContextCompat.getColor(
                it,
                R.color.card_background_unselected
            )
        }
        val color = if (selected) selectedColorID else unselectedColorID
        val border = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            if (selected)
                cornerRadius = 5.0f //20.0f
            if (color != null) {
                setStroke(4, color)
            }
        }
        imageView?.background = border
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardview -> {
                Timber.d("presenter >> onClick: " + v.tag)
                val cardInfo = v.tag as CardInfo
                cardInfo.getViewHolder().playerManager?.stopContent()
                EventBus.getDefault().post(CardClickedEvent(cardInfo.getCard(), cardRow))
            }
        }
    }


    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        return listener.onKey(view, keyCode, event)
    }

    inner class CardViewHolder(itemCategory: Types.CardRowCategory?, view: View) :
        ViewHolder(view) {
        val cardview: ImageView
        val playerView: PlayerView
        val gradient: ImageView
        val primeTag: ImageView
        val cardContainer: RelativeLayout
        val title: TextView
        val time: TextView
        val textHolder: RelativeLayout
        var liveTag: ImageView? = null
        var subTitle: TextView? = null
        var playerManager: PlayerManager? = null
        val expandableTextHolder: LinearLayout
        val expandableTextHolderFirst: TextView
        val expandableTextHolderMiddle: TextView
        val expandableTextHolderLast: TextView

        init {
            cardview = view.findViewById(R.id.cardview)
            playerView = view.findViewById(R.id.player_view)
            primeTag = view.findViewById(R.id.imageview_prime_Tag)
            gradient = view.findViewById(R.id.gradient)
            cardContainer = view.findViewById(R.id.rel_card_container)
            textHolder = view.findViewById(R.id.text_holder)
            title = view.findViewById(R.id.title)
            time = view.findViewById(R.id.time)
            expandableTextHolder = view.findViewById(R.id.expandable_text_holder)
            expandableTextHolderFirst = view.findViewById(R.id.expandable_text_holder_first)
            expandableTextHolderMiddle = view.findViewById(R.id.expandable_text_holder_middle)
            expandableTextHolderLast = view.findViewById(R.id.expandable_text_holder_last)
            subTitle = view.findViewById(R.id.sub_title)
            liveTag = view.findViewById(R.id.imageview_live_tag)
            //default setting
            resetCardComponents(this)
        }
    }

    private fun resetCardComponents(holder: CardViewHolder) {
        holder.gradient.visibility = View.GONE
        holder.primeTag.visibility = View.GONE
        holder.liveTag?.visibility = View.GONE
        holder.playerView.visibility = View.GONE
        holder.expandableTextHolder.visibility = View.GONE
    }

    inner class CardInfo(
        private val viewHolder: CardViewHolder,
        private val data: Card
    ) {
        fun getViewHolder(): CardViewHolder = viewHolder
        fun getCard(): Card = data
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        val holder = viewHolder as CardViewHolder?
        holder?.playerManager?.stopContent()
        holder?.playerManager = null
    }
}