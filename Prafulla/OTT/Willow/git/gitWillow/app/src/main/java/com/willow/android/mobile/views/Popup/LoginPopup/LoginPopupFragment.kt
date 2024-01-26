package com.willow.android.mobile.views.popup.loginPopup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.willow.android.databinding.LoginPopupFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity

class LoginPopupFragment : Fragment() {
    val CLOSE_LOGIN_PAGE = 0

    companion object {
        fun newInstance() = LoginPopupFragment()
    }

    private lateinit var viewModel: LoginPopupViewModel
    private lateinit var binding: LoginPopupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginPopupFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginPopupViewModel::class.java)

        binding.continueButton.setOnClickListener {
            if (binding.inputEmail.text.isNotEmpty() && binding.inputPassword.text.isNotEmpty() && binding.inputName.text.isNotEmpty()) {
                binding.spinner.visibility = View.VISIBLE
                makeSignupRequest()
            } else if ((!binding.inputName.isVisible) && binding.inputEmail.text.isNotEmpty() && binding.inputPassword.text.isNotEmpty()) {
                binding.spinner.visibility = View.VISIBLE
                makeLoginRequest()
            } else if ((!binding.inputPassword.isVisible) && binding.inputEmail.text.isNotEmpty()) {
                binding.spinner.visibility = View.VISIBLE
                checkExistingEmailRequest()
            } else {
                showMessage(MessageConfig.emptyCredentials)
            }
        }
    }

    fun checkExistingEmailRequest() {
        val email = binding.inputEmail.text.toString().trim()
        if (Utils.isInvalidEmail(email)) {
            showMessage(MessageConfig.wrongEmail)
            return
        }

        viewModel.checkExistingEmailRequest(requireContext(), email)
        viewModel.checkExistingEmailResponseData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            if (it.status) {
                binding.inputPassword.visibility = View.VISIBLE
            } else {
                binding.inputPassword.visibility = View.VISIBLE
                binding.inputName.visibility = View.VISIBLE
            }
        })
    }

    fun makeLoginRequest() {
        val email = binding.inputEmail.text.toString().trim()
        if (Utils.isInvalidEmail(email)) {
            showMessage(MessageConfig.wrongEmail)
            return
        }

        viewModel.makeLoginRequest(requireContext(), email, binding.inputPassword.text.toString())
        viewModel.loginResponseData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            if (it.result.status.lowercase() == "success") {
                UserModel.setLoginResponseData(it.result)

                activity?.setResult(CLOSE_LOGIN_PAGE)
                activity?.finish()
            } else {
                showMessage(it.result.message)
            }

        })
    }

    fun makeSignupRequest() {
        val email = binding.inputEmail.text.toString().trim()
        if (Utils.isInvalidEmail(email)) {
            showMessage(MessageConfig.wrongEmail)
            return
        }

        viewModel.makeSignupRequest(requireContext(), email, binding.inputPassword.text.toString(), binding.inputName.text.toString())
        viewModel.signupResponseData.observe(viewLifecycleOwner, Observer {
            binding.spinner.visibility = View.GONE

            if (it.result.status.lowercase() == "success") {
                AnalyticsService.sendUserSignupEvent()
                UserModel.setLoginResponseData(it.result)

                activity?.setResult(CLOSE_LOGIN_PAGE)
                activity?.finish()
            } else {
                showMessage(it.result.message)
            }
        })
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}