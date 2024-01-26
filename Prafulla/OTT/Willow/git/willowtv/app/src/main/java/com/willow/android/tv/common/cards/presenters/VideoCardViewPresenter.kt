package com.willow.android.tv.common.cards.presenters

import android.graphics.Typeface
import android.view.View
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

class VideoCardViewPresenter(listener: KeyListener, private val cardSpec: PresenterSelector.CardSpec, private val type: CardRow)
    : DefaultCardViewPresenter(listener, type) {

    override fun onBindViewHolder(viewHolder: ViewHolder?, item: Any?) {
        width = cardSpec.width
        height = cardSpec.height
        marginStart = cardSpec.marginStart
        marginEnd = cardSpec.marginEnd
        marginTop = cardSpec.marginTop
        marginBottom = cardSpec.marginBottom
        imagePlaceHolder = cardSpec.imagePlaceHolder
        val holder = viewHolder as CardViewHolder
        val data = item as Card
        holder.title.text = data.title
        holder.cardview.scaleType = cardSpec.scaleType
        holder.gradient.visibility = cardSpec.gradient
        if(item.content_type?.contains( Types.Content.LIVE.name, true) == true){
            holder.subTitle?.text = data.short_name
            holder.title.setTypeface(holder.title.typeface, Typeface.BOLD)
            if (item.isShowLiveTag()) {
                holder.liveTag?.visibility = View.VISIBLE
            }
        }else if(item.content_type?.contains( Types.Content.MATCH.name, true) == true){

        }else if(item.content_type?.contains( Types.Content.AD.name, true) == true){

        }else{
            holder.time.text = data.duration
        }
        isExpandableCard = cardSpec.isExpandableCard
        isExpandedCard = cardSpec.isExpandedCard
        url = if(isExpandableCard){
            data.getThumbnailPortrait()
        }else if(isExpandedCard) {
            data.getThumbnailHRB()
        }else{
            data.getThumbnailMDB()
        }
        if(isExpandableCard || isExpandedCard){
            holder.expandableTextHolderFirst.text = data.title
            holder.expandableTextHolderMiddle.text = data.sub_title
            val time = data.getSeriesStartAndEndTime()
            if(!time.isNullOrEmpty()) {
                holder.expandableTextHolderLast.text = time
            }

            holder.title.visibility = View.GONE
        }
        super.onBindViewHolder(viewHolder, item)

    }
}