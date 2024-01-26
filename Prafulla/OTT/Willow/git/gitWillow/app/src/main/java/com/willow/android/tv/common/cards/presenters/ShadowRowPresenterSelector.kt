package com.willow.android.tv.common.cards.presenters

import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.PresenterSelector
import com.willow.android.tv.common.cards.CardListRow
import com.willow.android.tv.data.repositories.commondatamodel.CardRow

/**
 * This [PresenterSelector] will return a [ListRowPresenter] which has shadow support
 * enabled or not depending on [CardRow.useShadow] for a given row.
 */
class ShadowRowPresenterSelector : PresenterSelector() {
    private val mShadowEnabledRowPresenter = ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE)
    private val mShadowDisabledRowPresenter = ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE)

    init {
        mShadowEnabledRowPresenter.setNumRows(1)
        mShadowDisabledRowPresenter.shadowEnabled = false

    }

    override fun getPresenter(item: Any): Presenter {
        if (item !is CardListRow) return mShadowDisabledRowPresenter
        val listRow: CardListRow = item as CardListRow
        val row: CardRow = listRow.getCardRow()
        return if (row.useShadow()) mShadowEnabledRowPresenter else mShadowDisabledRowPresenter
    }

    override fun getPresenters(): Array<Presenter> {
        return arrayOf(
            mShadowDisabledRowPresenter,
            mShadowEnabledRowPresenter
        )
    }
}