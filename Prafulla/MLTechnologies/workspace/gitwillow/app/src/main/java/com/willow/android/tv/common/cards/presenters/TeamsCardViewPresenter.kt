package com.willow.android.tv.common.cards.presenters

import android.view.View
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.commondatamodel.Card
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

class TeamsCardViewPresenter(
    listener: KeyListener,
    private val cardSpec: PresenterSelector.CardSpec,
    type: CardRow
) : DefaultCardViewPresenter(listener, type) {

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
       // holder.title.text = data.title
        holder.cardview.scaleType = cardSpec.scaleType
        holder.gradient.visibility = View.GONE
        url = data.getThumbnailMDB()
        super.onBindViewHolder(viewHolder, item)

    }
}