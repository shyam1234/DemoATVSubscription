package com.willow.android.tv.ui.subscription

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.willow.android.R
import com.willow.android.databinding.FragmentCancelSubscriptionBinding
import com.willow.android.tv.common.base.BaseFullScreenDialogFragment
import com.willow.android.tv.ui.subscription.viewmodel.SubscriptionViewModel


class UnsubscriptionDilaogFragment() : BaseFullScreenDialogFragment() {

    private lateinit var mViewModel: SubscriptionViewModel
    private lateinit var binding: FragmentCancelSubscriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        initView()
    }

    private fun initView() {
        setGoLinkText()
    }

    private fun setGoLinkText() {
        val goTo = "Go to\n"
        val link = "https://www.willow.tv/cancel"
        val content = SpannableString(goTo + link)
        content.setSpan(UnderlineSpan(), goTo.length, content.length, 0)
        content.setSpan(context?.let {
            ForegroundColorSpan(ContextCompat.getColor(it, R.color.red))
        }, goTo.length, content.length, 0)
        binding.txtLoginWithTVDesc.setText(content)
    }

    private fun setViewModel() {
        mViewModel = ViewModelProvider(this)[SubscriptionViewModel::class.java]
    }

    companion object {

        @JvmStatic
        fun newInstance() = UnsubscriptionDilaogFragment()
    }

}