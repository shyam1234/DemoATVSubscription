package com.willow.android.mobile.views.pages.tVELoginPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.databinding.TveLoginPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.ReloadService


class TVELoginPageFragment : Fragment() {
    companion object {
        fun newInstance() = TVELoginPageFragment()
    }

    private lateinit var binding: TveLoginPageFragmentBinding
    private lateinit var viewModel: TVELoginPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TveLoginPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setPageTitle()
        binding.spinner.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this).get(TVELoginPageViewModel::class.java)

        viewModel.getTVEConfigData(requireContext())
        viewModel.tveConfigData.observe(viewLifecycleOwner, Observer  {
            val categoryAdapter = TVELoginPageAdapter(requireContext(), it, this)
            val categoryLinearLayoutManager = LinearLayoutManager(requireContext())
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.tveLoginPageRecycler.layoutManager = categoryLinearLayoutManager
            binding.tveLoginPageRecycler.adapter = categoryAdapter

            TVELoginService.initializeAccessEnabler(this, requireContext(), false)
            binding.spinner.visibility = View.GONE
        })
    }

    fun verifyTVEUserFromWillow(token: String) {
        viewModel = ViewModelProvider(this).get(TVELoginPageViewModel::class.java)

        viewModel.verifyTVEUserData(requireContext(), token)
        viewModel.tveUserData.observe(viewLifecycleOwner, Observer {
            UserModel.setTVELoginResponseData(it)
            ReloadService.reloadAllScreens()

            activity?.setResult(ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
            activity?.finish()
        })
    }

    private fun setPageTitle() {
        binding.tveLoginPageHeader.pageHeaderTitle.text = "Select Provider"
        binding.otherNetworkText.text = MessageConfig.tveLoginInstruction
    }

    fun showSpinner() {
        binding.spinner.visibility = View.VISIBLE
    }
}