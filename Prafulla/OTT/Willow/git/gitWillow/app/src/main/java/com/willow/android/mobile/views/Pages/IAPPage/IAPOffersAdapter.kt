package com.willow.android.mobile.views.pages.iAPPage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.databinding.CardImageBinding
import com.willow.android.tv.utils.ImageUtility

class IAPOffersAdapter(val context: Context, val offerImagess: MutableList<String>): RecyclerView.Adapter<IAPOffersAdapter.OffersViewHolder>() {

    inner class OffersViewHolder(val binding: CardImageBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(offerImages: String) {
            ImageUtility.loadImageInto(offerImages,binding.cardImage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffersViewHolder {
        return OffersViewHolder(CardImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: OffersViewHolder, position: Int) {
        val sectionData = offerImagess[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return offerImagess.size
    }
}