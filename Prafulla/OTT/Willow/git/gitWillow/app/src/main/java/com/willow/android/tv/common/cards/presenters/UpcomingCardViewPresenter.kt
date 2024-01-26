package com.willow.android.tv.common.cards.presenters

import android.view.View
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

class UpcomingCardViewPresenter(listener: KeyListener, private val cardSpec: PresenterSelector.CardSpec, type: CardRow)
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
        holder.cardview.scaleType = cardSpec.scaleType
        holder.gradient.visibility = cardSpec.gradient
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
            //need to convert time as per Figma date style
            //holder.expandableTextHolderLast.text ="${data.getSeriesStartLocalTimeInMonthDate()} to ${data.getSeriesEndLocalTimeInMonthDateYear()}"
            val time = data.getSeriesStartAndEndTime()
            if(!time.isNullOrEmpty()) {
                holder.expandableTextHolderLast.text = time
            }
            holder.title.visibility = View.GONE
        }else{
            holder.title.text = data.title
            holder.title.visibility = View.VISIBLE
        }

        super.onBindViewHolder(viewHolder, item)

    }
}