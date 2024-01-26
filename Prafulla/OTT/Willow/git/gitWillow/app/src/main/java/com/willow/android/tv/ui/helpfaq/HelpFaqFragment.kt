package com.willow.android.tv.ui.helpfaq

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.willow.android.R
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentHelpFaqBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.cards.KeyListener
import com.willow.android.tv.data.repositories.helpfaq.datamodel.ApiHelpDataModel
import com.willow.android.tv.data.repositories.helpfaq.datamodel.Setting
import com.willow.android.tv.data.repositories.tvconfig.datamodel.NavigationTabsDataModel
import com.willow.android.tv.ui.helpfaq.adapter.HelpFaqMenuAdapter
import com.willow.android.tv.ui.helpfaq.viewmodel.HelpFaqViewModel
import com.willow.android.tv.ui.helpfaq.viewmodel.HelpFaqViewModelFactory
import com.willow.android.tv.utils.ErrorType
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.hide
import com.willow.android.tv.utils.show

class HelpFaqFragment() : BaseFragment(),
    KeyListener {
    private var data: NavigationTabsDataModel? = null
    private var mBinding: FragmentHelpFaqBinding? = null
    private lateinit var mViewModel: HelpFaqViewModel
    private var adapter: HelpFaqMenuAdapter? = null

    companion object {
        @JvmStatic
        fun newInstance(data: NavigationTabsDataModel?) =
            HelpFaqFragment().apply {
                this.data = data
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHelpFaqBinding.inflate(inflater.cloneInContext(context), container, false)
        mBinding?.lifecycleOwner = this
        return mBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationMenuCallback?.navMenuScreenName(Types.ScreenName.HELP)
        mViewModel = ViewModelProvider(
            this,
            HelpFaqViewModelFactory(WillowApplication.instance, data)
        )[(HelpFaqViewModel::class.java)]
        mBinding?.viewModel = mViewModel
        initListener()
        initObserver()
    }

    private fun initListener() {
        mBinding?.scrollView?.setOnKeyListener { view, i, keyEvent -> onKey(view, i, keyEvent) }
    }

    private fun initObserver() {
        mViewModel.apply {
            renderPage.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> onSuccess(it.data)
                    is Resource.Error -> {
                        showErrorPage(ErrorType.NO_DATA_FOUND)
                        navigationMenuCallback?.navMenuToggle(true)
                    }
                    is Resource.Loading -> onLoading()
                }
            }
        }
    }


    private fun onSuccess(helpData: ApiHelpDataModel?) {
        helpData?.let {
            if (it.settings.isNotEmpty()) {
                mBinding?.loadingview?.layoutLoading?.hide()
                initRecyclerView(it.settings)
            }
        }
    }

    private fun initRecyclerView(settingList: List<Setting>) {
        mBinding?.rvNavSettingItem?.layoutManager = LinearLayoutManager(context)
        adapter = HelpFaqMenuAdapter(settingList, menuItemClickListener, this)
        mBinding?.rvNavSettingItem?.adapter = adapter

    }

    private val menuItemClickListener = object : HelpFaqMenuAdapter.ItemClickListener {
        override fun itemClicked(position: Int, model: Setting) {
            mBinding?.scrollView?.scrollTo(0, 0)
            mViewModel.settingTitle.value = model.title
            mViewModel.settingDescription.value = model.description
            mViewModel.showHideTveProviderImage(model)
        }

    }

    private fun onLoading() {
        mBinding?.loadingview?.layoutLoading?.show()
        val imageViewAnimator =
            ObjectAnimator.ofFloat(mBinding?.loadingview?.progressBar, View.ROTATION, 359f)
        imageViewAnimator.repeatCount = Animation.INFINITE
        imageViewAnimator.duration = 1000
        imageViewAnimator.start()
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (view?.id == R.id.scroll_view)
                        focusItem()
                    else
                        navigationMenuCallback?.navMenuToggle(true)
                    return true
                }
            }
        }
        return false
    }

    private fun showErrorPage(errorType: ErrorType, errorMessage: String? = null) {
        mBinding?.loadingview?.layoutLoading?.hide()
        showError(mBinding?.root, errorType, errorMessage , backBtnListener = {activity?.onBackPressed()}, btnText = "Back")
    }

    override fun focusItem() {
        adapter?.getSelectedButton()?.requestFocus()
        adapter?.getSelectedButton()?.isSelected = true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }


}