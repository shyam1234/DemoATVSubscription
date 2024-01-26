package com.willow.android.mobile.views.pages.tVELoginPage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willow.android.R
import com.willow.android.databinding.CardTveProviderBinding
import com.willow.android.mobile.models.TVEConfigModel
import com.willow.android.mobile.models.TVEProviderConfigModel
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity
import com.willow.android.tv.utils.ImageUtility

class TVELoginPageAdapter(val context: Context, val tveConfigModel: TVEConfigModel, val fragment: TVELoginPageFragment): RecyclerView.Adapter<TVELoginPageAdapter.TVELoginPageViewHolder>() {
    inner class TVELoginPageViewHolder(val binding: CardTveProviderBinding): RecyclerView.ViewHolder(binding.root) {
        fun setData(sectionData: TVEProviderConfigModel) {
            ImageUtility.loadImageDontTransform(
                imageUrl = sectionData.image,
                placeHolder=  R.drawable.tbc,
                view = binding.tveProviderImage)


            binding.tveProviderImage.setOnClickListener {
                if (sectionData.isAvailable) {
                    initTVELoginForSelectedProvider(sectionData.id)
                    fragment.showSpinner()
                } else {
                    showMessage(sectionData.message)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVELoginPageViewHolder {
        return TVELoginPageViewHolder(CardTveProviderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TVELoginPageViewHolder, position: Int) {
        val sectionData = tveConfigModel.tve_providers[position]
        holder.setData(sectionData)
    }

    override fun getItemCount(): Int {
        return tveConfigModel.tve_providers.size
    }

    private fun initTVELoginForSelectedProvider(providerName: String) {
        TVELoginService.initProviderLoginProcess(providerName)
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        context.startActivity(intent)
    }
}