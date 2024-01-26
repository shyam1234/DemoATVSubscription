package com.willow.android.tv.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.willow.android.R
import com.willow.android.databinding.FragmentLoginFirstBinding
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.changeColorOnFocusChange
import com.willow.android.tv.utils.config.GlobalTVConfig
import com.willow.android.tv.utils.extension.loadImageUrl
import com.willow.android.tv.utils.extension.navigateSafe
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFirstFragment : Fragment() {

    private var _binding: FragmentLoginFirstBinding? = null
    lateinit var prefRepository: PrefRepository


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefRepository = PrefRepository(requireContext())

        binding.imgFooter1.loadImageUrl(GlobalTVConfig.getTVProviderLogosURL(),R.drawable.default_billboard_holder)

        binding.buttonEmailLogin.setOnClickListener {
            findNavController().navigateSafe(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonSignup.setOnClickListener {
            findNavController().navigateSafe(R.id.action_First_to_SignupFragment)
        }
        binding.buttonTvLogin.setOnClickListener {
            val intent = Intent(activity, TVEActivity::class.java)
            getResult.launch(intent)
        }
        binding.buttonEmailLogin.changeColorOnFocusChange()
        binding.buttonEmailLogin.requestFocus()
        binding.buttonTvLogin.changeColorOnFocusChange()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getBooleanExtra("tveLoginSuccess",false)
                Timber.tag("TAG").i(": %s", value)


                findNavController().navigateSafe(R.id.action_LoginActivity_to_MainActivity)
                activity?.finish()


            }
        }
}