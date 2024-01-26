package com.willow.android.tv.ui.explorepage.model

import android.os.Parcelable
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.carousel.models.HeroBanner
import com.willow.android.tv.data.repositories.commondatamodel.CardRow
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExplorePageModel(
    private val heroBannerModel: HeroBanner?,
    val cardRowModel: ArrayList<CardRow>?
):Parcelable{
    fun getCarouselData() : HeroBanner?{
        return heroBannerModel?.also { model ->
            val iterator = model.listItems?.iterator()
            while (iterator?.hasNext() == true) {
                val card = iterator.next()
                //remove item if content_type is 'na'
                if (card.content_type?.uppercase() == "NA") {
                    iterator.remove()
                }
            }
        }
    }

    /**
     * for validating the given content type with whitelisted Types.Content
     */
    private fun isEnumStringMatch(input: String?): Boolean {
        val enumValues = Types.Content.values()
        for (enumValue in enumValues) {
            if (enumValue.name == input) {
                return true
            }
        }
        return false
    }
}

