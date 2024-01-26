package com.willow.android.tv.ui.login

import android.os.Bundle
import android.view.WindowManager
import com.willow.android.databinding.ActivityLoginBinding
import com.willow.android.tv.common.base.BaseActivity
import com.willow.android.tv.ui.playback.PlayerManager


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerManager.shouldPlayContent = false
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerManager.shouldPlayContent = true
    }






}