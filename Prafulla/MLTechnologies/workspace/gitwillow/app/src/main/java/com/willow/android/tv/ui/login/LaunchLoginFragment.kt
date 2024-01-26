package com.willow.android.tv.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.willow.android.R
import com.willow.android.databinding.FragmentLaunchLoginBinding
import com.willow.android.tv.ui.login.viewmodel.LoginViewModel
import com.willow.android.tv.utils.changeColorOnFocusChange
import com.willow.android.tv.utils.extension.navigateSafe


class LaunchLoginFragment : Fragment() {
    private lateinit var _binding: FragmentLaunchLoginBinding
    private var loginViewModel: LoginViewModel? = null

    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLaunchLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProviders.of(requireActivity())[LoginViewModel::class.java]

        binding.buttonSignInSignup.changeColorOnFocusChange()
        binding.buttonSignInSignup.setOnClickListener {
            findNavController().navigateSafe(R.id.action_Launch_to_FirstFragment)

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LaunchLoginFragment()
    }
}