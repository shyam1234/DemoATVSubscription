package com.willow.android.mobile.views.pages.forgotPasswordPage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.ForgotPasswordPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity



class ForgotPasswordPageFragment : Fragment() {
    companion object {
        fun newInstance() = ForgotPasswordPageFragment()
    }

    private lateinit var binding: ForgotPasswordPageFragmentBinding
    private lateinit var viewModel: ForgotPasswordPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ForgotPasswordPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setPageTitle()
        viewModel = ViewModelProvider(this).get(ForgotPasswordPageViewModel::class.java)

        binding.sendButton.setOnClickListener {
            if (binding.inputEmail.text.isNotEmpty()) {
                binding.spinner.visibility = View.VISIBLE

                val email = binding.inputEmail.text.toString()
                if (Utils.isInvalidEmail(email)) {
                    showMessage(MessageConfig.wrongEmail)
                } else {
                    viewModel.makeForgotPasswordRequest(requireContext(), email)
                    viewModel.forgotPasswordResponseData.observe(viewLifecycleOwner, Observer {

                        val intent = Intent()
                        intent.putExtra("MESSAGE", it.result.message)
                        activity?.setResult(ResultCodes.RESULT_FORGOT_PWD_MSG, intent)
                        activity?.finish()
                    })
                }
            }
        }
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }

    private fun setPageTitle() {
        binding.forgotPasswordHeader?.pageHeaderTitle?.text = "Forgot Password"
    }
}