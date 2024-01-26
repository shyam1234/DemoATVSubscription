package com.willow.android.tv.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.willow.android.R
import com.willow.android.databinding.ActivityTveBinding
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.TVEService
import com.willow.android.tv.utils.changeColorOnFocusChange
import com.willow.android.tv.utils.config.GlobalTVConfig

class TVEActivity : BaseActivity() {

    private lateinit var binding: ActivityTveBinding
    private lateinit var prefRepository: PrefRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        prefRepository = PrefRepository(applicationContext)
        binding.codeText.text= ""
        binding.titleText.text = GlobalTVConfig.getTVELoginInstruction()
        binding.continueButton.changeColorOnFocusChange()
        binding.continueButton.requestFocus()
        TVEService.setTVEActivity(this)
        TVEService.getRegCodeHeader()
        val button = findViewById<Button>(R.id.continue_button)
        button.setOnClickListener {
            binding.spinner.visibility = View.VISIBLE
            TVEService.getAuthNHeader()
        }
    }

    fun setRegCodeText(text: String?) {
        binding.codeText.text = text
        binding.spinner.visibility = View.INVISIBLE
    }

    fun storeAuthenticatedUser(
        userId: String?,
        tveDeviceId: String?,
        tveProvider: String?,
        adsCategory: String?,
        subscriptionStatus: Int,
        enableDfpForLive: Boolean,
        enableDfpForVOD: Boolean
    ) {


        prefRepository.setTVEUserID(userId.toString())
        prefRepository.setTVELoggedIn(true)
        prefRepository.setTVEProvider(tveProvider.toString())
        prefRepository.setTVEDeviceID(tveDeviceId.toString())
        prefRepository.setAdsCategory(adsCategory.toString())
        if(subscriptionStatus==1){
            prefRepository.setUserSubscribed(true)
        }
        prefRepository.setEnableDFPForVOD(enableDfpForVOD)
        prefRepository.setEnableDFPForLive(enableDfpForLive)


        binding.spinner.visibility = View.INVISIBLE


        val data = true
        val intent = Intent().apply {
            putExtra("tveLoginSuccess", data)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    fun showAuthError() {
//        FragmentHelpers.showErrorDialog(getFragmentManager(), "", Config.tveAuthErrorMsg);
        binding.spinner.visibility = View.INVISIBLE
    }

    fun showReceivedError(errorMesssage: String?) {
        Toast.makeText(applicationContext,errorMesssage,Toast.LENGTH_SHORT).show()
//        FragmentHelpers.showErrorDialog(getFragmentManager(), "", errorMesssage);
        binding.spinner.visibility = View.INVISIBLE
    }

    fun showError() {
//        FragmentHelpers.showErrorDialog(getFragmentManager(), "", Config.tveErrorMsg);
        binding.spinner.visibility = View.INVISIBLE
    }
}