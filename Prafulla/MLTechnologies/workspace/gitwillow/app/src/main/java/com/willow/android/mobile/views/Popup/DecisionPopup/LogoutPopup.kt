package com.willow.android.mobile.views.popup.decisionPopup

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.willow.android.databinding.PopupDecisionBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.auth.UserModel

class LogoutPopup : DialogFragment() {
    private lateinit var binding: PopupDecisionBinding

    var onResult: (() -> Unit)? = null

    companion object {}

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        binding = PopupDecisionBinding.inflate(layoutInflater)
        return binding.root
    }

    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.messageTitle.text = MessageConfig.logoutDecision
        binding.noButton.setOnClickListener { dismiss() }
        binding.yesButton.setOnClickListener {
            UserModel.logout(requireContext())
            onResult?.invoke()
            dismiss()
        }
    }
}