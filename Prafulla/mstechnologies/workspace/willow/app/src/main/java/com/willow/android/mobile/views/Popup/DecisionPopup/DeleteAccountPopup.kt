package com.willow.android.mobile.views.popup.decisionPopup

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.willow.android.databinding.PopupDeleteAccountBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity

class DeleteAccountPopup : DialogFragment() {
    private lateinit var binding: PopupDeleteAccountBinding

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
        binding = PopupDeleteAccountBinding.inflate(layoutInflater)
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

        binding.messageTitle.text = MessageConfig.deleteAccountTitle
        binding.messageText.text = MessageConfig.deleteAccountSubtitle
        binding.messageTerms.text = MessageConfig.deleteAccountTerms
        binding.noButton.setOnClickListener { dismiss() }
        binding.yesButton.setOnClickListener {
            if (binding.messageTerms.isChecked) {
                onResult?.invoke()
                dismiss()
            } else {
                showMessage(MessageConfig.deleteAccountConsent)
            }
        }
    }

    fun showMessage(message: String) {
        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}