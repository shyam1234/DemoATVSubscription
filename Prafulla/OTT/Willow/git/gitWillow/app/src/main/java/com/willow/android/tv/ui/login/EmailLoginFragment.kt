package com.willow.android.tv.ui.login

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.willow.android.R
import com.willow.android.databinding.FragmentEmailLoginBinding
import com.willow.android.tv.data.repositories.loginpage.datamodel.APICheckAccountDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APIForgotPassDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.ui.login.viewmodel.LoginViewModel
import com.willow.android.tv.ui.main.MainActivity
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.changeColorOnFocusChange
import com.willow.android.tv.utils.extension.launchActivity
import com.willow.android.tv.utils.extension.navigateSafe
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show
import com.willow.android.tv.utils.toast
import timber.log.Timber


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EmailLoginFragment : Fragment() {

    private var _binding: FragmentEmailLoginBinding? = null
    private var loginViewModel: LoginViewModel? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var showHideKeyboard = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEmailLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProviders.of(requireActivity())[LoginViewModel::class.java]
        binding.lifecycleOwner= this
        binding.loginViewModel= loginViewModel

        binding.buttonSecond.setOnClickListener {
            findNavController().navigateSafe(R.id.action_SecondFragment_to_FirstFragment)
        }
        binding.buttonSecond.changeColorOnFocusChange()
        binding.emailTextview.requestFocus()


        loginViewModel?.loginStatus()?.observe(viewLifecycleOwner){ loginStatus->

            if(loginStatus?.result == true){
                activity?.launchActivity<MainActivity> {}
            }else{
                if(loginStatus?.emailEmpty==true){
                    binding.emailTextview.error = "Enter a Valid E-mail Address"
                    if(loginStatus?.passWordEmpty==true) {
                        binding.passwordTextview.error = "Enter a Password"
                    }

                }
                if(loginStatus!=null)
                Toast.makeText(view.context, loginStatus.message, Toast.LENGTH_SHORT).show()

            }


        }

        loginViewModel?.renderPage?.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> onSuccess(it.data)
                is Resource.Error -> requireContext().toast(it.message.toString())
                is Resource.Loading -> onLoading()
            }
        }

        loginViewModel?.renderPageCheckAcc?.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> onSuccessCheckUser(it.data)
                is Resource.Error -> requireContext().toast(it.message.toString())
                is Resource.Loading -> onLoading()
            }
        }

     loginViewModel?.renderPageForgotPass?.observe(viewLifecycleOwner){
            when(it){
                is Resource.Success -> onSuccessForgotPass(it.data)
                is Resource.Error -> requireContext().toast(it.message.toString())
                is Resource.Loading -> onLoading()
            }
        }



        binding.emailTextview.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                hideSoftKeyboard(requireActivity(),v)
                setLayoutBackground(binding.iconLoginEmail,R.drawable.bg_login_icon)
                if(!binding.emailTextview.text.isEmpty() ) {
                    if( Patterns.EMAIL_ADDRESS.matcher(binding.emailTextview.text.toString()).matches())
                        loginViewModel?.checkEmailExists(binding.emailTextview.text.toString())
                    else {
                        binding.emailTextview.error = "Enter a Valid E-mail Address"
                    }
                }else{

                }
            }else{
                setLayoutBackground(binding.iconLoginEmail,R.drawable.bg_login_icon_focussed)
                showSoftKeyboard(requireActivity(),v)
            }
        }

        binding.txtForgotPass.setOnClickListener {
            if(!binding.emailTextview.text.isEmpty())
                loginViewModel?.onClickForgotPassword(binding.emailTextview.text.toString())
            else{
                binding.emailTextview.error = "Enter a Valid E-mail Address"
                binding.emailTextview.requestFocus()
            }

        }

        binding.passwordTextview.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus){
                setLayoutBackground(binding.iconLoginPassword,R.drawable.bg_login_icon)
                hideSoftKeyboard(requireActivity(),v)
            }else{
                setLayoutBackground(binding.iconLoginPassword,R.drawable.bg_login_icon_focussed)
                showSoftKeyboard(requireActivity(),v)
            }
        }

    }

    private fun onSuccessForgotPass(data: APIForgotPassDataModel?) {
        binding.loadingview.layoutLoading.hide()

        Toast.makeText(requireContext(),data?.result?.message.toString(),Toast.LENGTH_SHORT).show()


    }

    private fun onSuccessCheckUser(data: APICheckAccountDataModel?) {
        binding.loadingview.layoutLoading.hide()
        binding.passwordTextview.isEnabled=true

        if(data?.status == true)
            loginViewModel?.loginOrRegister?.value = "login"
        else
            loginViewModel?.loginOrRegister?.value = "register"


    }


    private fun onSuccess(data: APILoginDataModel?) {
        binding.loadingview.layoutLoading.hide()

        loginViewModel!!.bindAPIDataToPageModel(data)
       // GaAnalytics.sendLoginEvent()
    }

    private fun onLoading() {
        binding.loadingview.layoutLoading.show()
        val imageViewAnimator = ObjectAnimator.ofFloat(binding.loadingview.progressBar,View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    fun hideSoftKeyboard(activity: Activity, view: View?) {
        if(showHideKeyboard) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                Timber.d("hideSoftKeyboard(requireActivity())")

                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
    fun showSoftKeyboard(activity: Activity, view: View?) {
        if(showHideKeyboard) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun setLayoutBackground(layout: View, drawableResId: Int) {
        val drawable = ContextCompat.getDrawable(layout.context, drawableResId)
        layout.background = drawable
    }

    override fun onStop() {
        super.onStop()
        showHideKeyboard = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        loginViewModel?.clearLiveData()


    }



//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMessageEvent(event: KeyPressedEvent) {
//        Toast.makeText(activity, event.keyCode.toString()+"::"+event.keyEvent, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        EventBus.getDefault().register(this)
//    }
//
//    override fun onStop() {
//        EventBus.getDefault().unregister(this)
//        super.onStop()
//    }
}