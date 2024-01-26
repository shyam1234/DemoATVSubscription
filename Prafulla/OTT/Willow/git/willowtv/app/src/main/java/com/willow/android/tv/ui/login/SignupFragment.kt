package com.willow.android.tv.ui.login

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.willow.android.R
import com.willow.android.databinding.FragmentSignupBinding
import com.willow.android.tv.data.repositories.signuppage.datamodel.APISIgnupDataModel
import com.willow.android.tv.ui.login.viewmodel.LoginViewModel
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import com.willow.android.tv.utils.toast
import java.util.Objects


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private var loginViewModel: LoginViewModel? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProviders.of(requireActivity())[LoginViewModel::class.java]
        binding.lifecycleOwner= this
        binding.loginViewModel= loginViewModel


        loginViewModel!!.getSignupUser()!!.observe(viewLifecycleOwner
        ) { signUpUser ->
            if (TextUtils.isEmpty(Objects.requireNonNull(signUpUser)?.email)) {
                binding.emailTextview.error = "Enter an E-Mail Address"
                binding.emailTextview.requestFocus()
            } else if (!signUpUser!!.isEmailValid) {
                binding.emailTextview.error = "Enter a Valid E-mail Address"
                binding.emailTextview.requestFocus()
            } else if (TextUtils.isEmpty(Objects.requireNonNull(signUpUser)?.password)) {
                binding.passwordTextview.error = "Enter a Password"
                binding.passwordTextview.requestFocus()
            } else if (!signUpUser!!.isPasswordLengthGreaterThan5) {
                binding.passwordTextview.error = "Enter at least 6 Digit password"
                binding.passwordTextview.requestFocus()
            } else if (TextUtils.isEmpty(Objects.requireNonNull(signUpUser)?.name)) {
                binding.emailTextview.error = "Enter a user name"
                binding.emailTextview.requestFocus()
            }
        }

        loginViewModel!!.loginStatus()!!.observe(viewLifecycleOwner){ loginStatus->

            if(loginStatus?.result == true){
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_login).navigate(R.id.action_SignupFragment_to_FirstFragment)

            }else{
                Toast.makeText(view.context, loginStatus?.message, Toast.LENGTH_SHORT).show()

            }

        }

        loginViewModel!!.renderPageSignup.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> onSuccess(it.data)
                is Resource.Error -> requireContext().toast(it.message.toString())
                is Resource.Loading -> onLoading()
            }
        }
    }

    private fun onSuccess(data: APISIgnupDataModel?) {
        binding.loadingview.layoutLoading.hide()

        loginViewModel!!.bindSignUpAPIDataToPageModel(data)
    }

    private fun onLoading() {
        binding.loadingview.layoutLoading.show()
        val imageViewAnimator = ObjectAnimator.ofFloat(binding.loadingview.progressBar,View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        loginViewModel?.clearLiveData()

    }
}