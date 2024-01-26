package com.willow.android.tv.ui.accountDetails

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.willow.android.WillowApplication
import com.willow.android.databinding.FragmentAccountDetailBinding
import com.willow.android.tv.common.Types
import com.willow.android.tv.common.base.BaseFragment
import com.willow.android.tv.common.genericDialogBox.IGenericDialogClickListener
import com.willow.android.tv.data.room.db.AppDatabase
import com.willow.android.tv.ui.accountDetails.viewModel.AccountDetailsViewModel
import com.willow.android.tv.ui.accountDetails.viewModel.AccountDetailsViewModelFactory
import com.willow.android.tv.ui.login.LoginActivity
import com.willow.android.tv.ui.main.MainActivity
import com.willow.android.tv.ui.subscription.SubscriptionActivity
import com.willow.android.tv.ui.subscription.UnsubscriptionDilaogFragment
import com.willow.android.tv.utils.NavigationUtils
import com.willow.android.tv.utils.UserDetailsHelper
import com.willow.android.tv.utils.extension.launchActivity
import com.willow.android.tv.utils.extension.startActivityWithOutData
import com.willow.android.tv.utils.setBackgroundSelecter

class AccountDetailsFragment : BaseFragment(), View.OnKeyListener {
    private var mViewModel: AccountDetailsViewModel? = null
    private lateinit var binding: FragmentAccountDetailBinding
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationMenuCallback?.navMenuScreenName(Types.ScreenName.PROFILE)
        appDatabase = WillowApplication.dbBuilder
        mViewModel = ViewModelProvider(
            this,
            AccountDetailsViewModelFactory(WillowApplication.instance, appDatabase)
        )[AccountDetailsViewModel::class.java]
        mViewModel?.checkSubscription()
        binding.model = mViewModel?.getAccountDetailModel(context)
        initView()
    }

    override fun focusItem() {
        //Nothing
    }

    private fun initView() {
        initViewFocus()
        initListener()
    }

    private fun initViewFocus() {
        binding.buttonLogOut.setBackgroundSelecter()
        binding.buttonCancelSubscription.setBackgroundSelecter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.buttonLogOut.focusable = View.FOCUSABLE
        } else {
            binding.buttonLogOut.isFocusable = true
        }
        binding.buttonLogOut.setOnKeyListener(this)
        binding.buttonLogOut.isFocusable = true
    }

    private fun initListener() {
        binding.buttonLogOut.setOnClickListener {
            if (binding.model?.isUserLogin == true) {
                onLogoutBtnClicked()
            } else {
                requireContext().launchActivity<LoginActivity>()
            }
        }
        binding.buttonCancelSubscription.setOnClickListener {
            if (binding.model?.subscriptionStatus == true)
                onSubscripeBtnClick()
            else
                launchSubscripeActivity()
        }
    }

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            navigationMenuCallback?.navMenuToggle(true)
        }
        return false
    }


    private fun launchSubscripeActivity() {
        requireContext().launchActivity<SubscriptionActivity>()
    }

    private fun onSubscripeBtnClick() {
        NavigationUtils.showGenericDialogFragment(
            activity,
            mViewModel?.getCancelSubscriptionDialogModel(),
            object : IGenericDialogClickListener {
                override fun onPositiveClick(view: View) {
                    (activity as MainActivity?)?.setToHomePage()
                }

                override fun onNegativeClick(view: View) {
                    NavigationUtils.showDialogFragment(
                        activity,
                        UnsubscriptionDilaogFragment.newInstance()
                    )
                }
            }
        )
    }

    private fun onLogoutBtnClicked() {
        NavigationUtils.showGenericDialogFragment(
            activity,
            mViewModel?.getLogOutDialogModel(),
            object : IGenericDialogClickListener {
                override fun onPositiveClick(view: View) {
                    UserDetailsHelper.logout(context)
                    mViewModel?.clearAllTablesOnUserLogout()

//                    NavigationUtils.navigateTo(
//                        activity,
//                        LoginActivity::class.java, true
//                    )
                    requireContext().startActivityWithOutData<MainActivity>(true)
                }

                override fun onNegativeClick(view: View) {
                }
            }
        )
    }

    companion object {

        @JvmStatic
        fun newInstance() = AccountDetailsFragment().apply {
            arguments = Bundle().apply {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel = null
        binding.unbind()
    }
}