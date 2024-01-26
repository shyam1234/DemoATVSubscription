package com.willow.android.tv.common.cards

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

/**
 * The {@link CardListRow} allows the {@link ShadowRowPresenterSelector} to access the {@link CardRow}
 * held by the row and determine whether to use a {@link androidx.leanback.widget.Presenter}
 * with or without a shadow.
 */
class CardListRow(
    headerItem: HeaderItem,
    private val rowAdapter: ArrayObjectAdapter,
    private val cardRow: CardRow
) : ListRow(headerItem, rowAdapter) {

    /**
     * one row card info
     */
    fun getCardRow(): CardRow {
        return cardRow
    }

    /**
     * one row adapter
     */
    fun getRowAdapter(): ArrayObjectAdapter{
        return rowAdapter
    }
}