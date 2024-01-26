package com.willow.android.mobile.views.pages.profilePage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.willow.android.R
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity
import tv.willow.Models.SettingsItemModel


class ProfilePageActivity : AppCompatActivity(), ProfileListenerInterface {
    private lateinit var viewModel: ProfilePageViewModel
    private var spinner: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page_activity)
        setPageTitle()

        viewModel = ViewModelProvider(this).get(ProfilePageViewModel::class.java)
        spinner = findViewById<ProgressBar>(R.id.spinner)

        val settingsItemModel = intent.getSerializableExtra("SETTINGS_ITEM") as? SettingsItemModel
        if (settingsItemModel != null) {
            val categoryAdapter = ProfilePageAdapter(this, settingsItemModel, this)
            val categoryLinearLayoutManager = LinearLayoutManager(this)
            categoryLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL

            val recycler: RecyclerView = this.findViewById(R.id.profile_page_recycler)
            recycler.layoutManager = categoryLinearLayoutManager
            recycler.adapter = categoryAdapter

            // Add divider decorator
            val  otherPageDecorator = DividerItemDecoration(this, RecyclerView.VERTICAL)
            recycler.addItemDecoration(otherPageDecorator)
        }
    }

    private fun setPageTitle() {
        val header = findViewById<View>(R.id.profile_page_header)
        val headerTitle = header.findViewById<TextView>(R.id.page_header_title)
        headerTitle.text = "Profile"
    }

    override fun requestPasswordListener() {
        spinner?.visibility = View.VISIBLE

        viewModel.makeForgotPasswordRequest(this, UserModel.email)
        viewModel.forgotPasswordResponseData.observe(this, Observer {
            spinner?.visibility = View.GONE
            showMessage(it.result.message)
        })
    }

    override fun verifyEmailListener() {
        spinner?.visibility = View.VISIBLE

        viewModel.makeVerifyEmailRequest(this, UserModel.email)
        viewModel.verifyEmailResponseData.observe(this, Observer {
            spinner?.visibility = View.GONE
            showMessage(it.result.message)
        })
    }

    override fun deleteAccountListener() {
        spinner?.visibility = View.VISIBLE

        if (UserModel.isGoogleUser) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, OnCompleteListener<Void?> {
                    makeDeleteAccountReqToWillowServer()
                })
        } else if (UserModel.isAppleUser) {

        } else {
            makeDeleteAccountReqToWillowServer()
        }

    }

    private fun makeDeleteAccountReqToWillowServer() {
        viewModel.makeDeleteAccountRequest(this, UserModel.email, UserModel.userId)
        viewModel.deleteAccountResponseData.observe(this, Observer {
            spinner?.visibility = View.GONE
            UserModel.logout(this)

            val intent = Intent()
            intent.putExtra("DELETE_ACC_MEG", it.result.message)
            setResult(ResultCodes.DELETE_ACCOUNT_RESULT, intent)
            finish()
        })
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(this, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}