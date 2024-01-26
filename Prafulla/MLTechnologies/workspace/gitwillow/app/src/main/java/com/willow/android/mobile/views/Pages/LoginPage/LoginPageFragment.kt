package com.willow.android.mobile.views.pages.loginPage

import android.R
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.willow.android.databinding.LoginPageFragmentBinding
import com.willow.android.mobile.configs.MessageConfig
import com.willow.android.mobile.configs.ResultCodes
import com.willow.android.mobile.configs.WiConfig
import com.willow.android.mobile.models.auth.UserModel
import com.willow.android.mobile.services.ReloadService
import com.willow.android.mobile.services.analytics.AnalyticsService
import com.willow.android.mobile.utils.Utils
import com.willow.android.mobile.views.pages.WebViewPageActivity
import com.willow.android.mobile.views.pages.forgotPasswordPage.ForgotPasswordPageActivity
import com.willow.android.mobile.views.pages.tVELoginPage.TVELoginPageActivity
import com.willow.android.mobile.views.popup.loginPopup.LoginPopupActivity
import com.willow.android.mobile.views.popup.messagePopup.MessagePopupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection


class LoginPageFragment : Fragment() {
    val TAG = "LoginPageFragment"

    var mGoogleSignInClient: GoogleSignInClient? = null

    // Sign in apple
    lateinit var appleAuthURLFull: String
    lateinit var appledialog: Dialog
    lateinit var dialogProgressbar: ProgressBar
    lateinit var appleAuthCode: String
    lateinit var appleClientSecret: String

    var appleId = ""
    var appleFirstName = ""
    var appleMiddleName = ""
    var appleLastName = ""
    var appleEmail = ""
    var appleAccessToken = ""

    private lateinit var binding: LoginPageFragmentBinding
    private lateinit var viewModel: LoginPageFragmentViewModel

    // sign in with apple
    companion object {
        fun newInstance() = LoginPageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginPageFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setPageTitle()
        viewModel = ViewModelProvider(this).get(LoginPageFragmentViewModel::class.java)

        binding.subscriptionView.priceTextView.text = UserModel.iapProductPrice
        binding.tveProvidersList.text = WiConfig.tveProvidersList
        addButtonListeners()
        setupPrivacyAndTermsLinks()
        sendAnalyticsEvent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureGoogleSignIn()
    }

    override fun onStart() {
        super.onStart()

        checkExistingGoogleUser()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ResultCodes.RC_SIGN_IN) {
            gotGoogleUserInfo(data)
            Log.d("REQUEST", requestCode.toString())
        }

        if (requestCode == ResultCodes.RESULT_CLOSE_LOGIN_PAGE) {
            activity?.setResult(ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
            activity?.finish()
        }

        if (requestCode == ResultCodes.RESULT_FORGOT_PWD_MSG) {
            val message: String? = data?.getStringExtra("MESSAGE")

            if ( (message != null) && message.isNotEmpty()) {
                val intent = Intent(context, MessagePopupActivity::class.java).apply {}
                intent.putExtra("MESSAGE", message)

                startActivity(intent)
            }
        }
    }

    private fun setPageTitle() {
        binding.loginPageHeader?.pageHeaderTitle?.text = "Login"
    }

    fun addButtonListeners() {
        binding.subscriptionView.subscribeButton.setOnClickListener { subscribeButtonClicked() }
        binding.loginButton.setOnClickListener { loginButtonClicked() }
        binding.appleLoginButton.setOnClickListener { loginWithAppleButtonClicked() }
        binding.googleLoginButton.setOnClickListener { loginWithGoogleButtonClicked() }
        binding.loginWithTvProviderButton.setOnClickListener { loginWithTVProviderButtonClicked() }
        binding.signUpButton.setOnClickListener { signupButtonClicked() }
        binding.forgotPasswordTV.setOnClickListener { forgotPasswordButtonClicked() }
    }

    private fun setupPrivacyAndTermsLinks() {
        binding.termsConditionsLink.setOnClickListener {
            showWebView(MessageConfig.termsOfUseUrl, "Terms of Use")
        }

        binding.privacyLink.setOnClickListener {
            showWebView(MessageConfig.privacyPolicyUrl, "Privacy Policy")
        }
    }

    private fun showWebView(url: String, pageTitle: String) {
        val intent = Intent(context, WebViewPageActivity::class.java).apply {}
        intent.putExtra("url", url)
        intent.putExtra("PAGE_TITLE", pageTitle)
        requireContext().startActivity(intent)
    }

    fun subscribeButtonClicked() {
        val intent = Intent(context, LoginPopupActivity::class.java).apply {}
        startActivityForResult(intent, ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
    }

    fun loginButtonClicked() {
        val intent = Intent(context, LoginPopupActivity::class.java).apply {}
        startActivityForResult(intent, ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
    }

    fun loginWithAppleButtonClicked() {
        binding.spinner.visibility = View.VISIBLE
        val timestampString = (System.currentTimeMillis() / 1000).toString()
        val state = Utils.getAppleLoginStateToken(timestampString) + "_" + timestampString + "_android"
        appleAuthURLFull = AppleConstants.AUTHURL + "?client_id=" + AppleConstants.CLIENT_ID + "&redirect_uri=" + AppleConstants.REDIRECT_URI + "&response_type=code%20id_token&scope=" + AppleConstants.SCOPE + "&response_mode=form_post&state=" + state
        setupAppleWebviewDialog(appleAuthURLFull)
    }

    fun loginWithGoogleButtonClicked() {
        binding.spinner.visibility = View.VISIBLE
        initGoogleSignIn()
    }

    private fun loginWithTVProviderButtonClicked() {
        val intent = Intent(context, TVELoginPageActivity::class.java).apply {}
        startActivityForResult(intent, ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
    }

    fun signupButtonClicked() {
        val intent = Intent(context, LoginPopupActivity::class.java).apply {}
        startActivityForResult(intent, ResultCodes.RESULT_CLOSE_LOGIN_PAGE)
    }

    fun forgotPasswordButtonClicked() {
        val intent = Intent(context, ForgotPasswordPageActivity::class.java).apply {}
        startActivityForResult(intent, ResultCodes.RESULT_FORGOT_PWD_MSG)
    }

    // ************* Google Login *************
    fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestServerAuthCode(Keys.googleLoginClientId, false)
//            .requestIdToken("1019408039286-c1okp250d3cp0dkslekg7rk35u1q4h13.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    fun checkExistingGoogleUser() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (account != null) {
            // Already google user logged in
            verifyGoogleLoginWithWillow(account)
        }
    }

    fun initGoogleSignIn() {
        if (mGoogleSignInClient != null) {
            val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, ResultCodes.RC_SIGN_IN)
        }
    }

    fun gotGoogleUserInfo(data: Intent?) {
        binding.spinner.visibility = View.GONE
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign in success")
            // Signed in successfully, show authenticated UI.
            UserModel.googleUserId = account.id.toString()
            UserModel.googleIDToken = account.idToken.toString()
            UserModel.googleGivenName = account.givenName.toString()
            UserModel.googleFamilyName = account.familyName.toString()
            UserModel.setEmailValue(account.email.toString())

            verifyGoogleLoginWithWillow(account)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    fun verifyGoogleLoginWithWillow(account: GoogleSignInAccount) {
        account.email?.let {
            viewModel.makeGoogleLoginRequest(requireContext(), it)
            viewModel.googleLoginResponseData.observe(viewLifecycleOwner) {
                if (it.result.status.lowercase() == "success") {
                    UserModel.setGoogleLoginResponseData(account.email!!, it.result)
                    ReloadService.reloadAllScreens()
                    activity?.finish()
                } else {
                    showMessage(it.result.message)
                }
            }
        }
    }


    // ************* Apple Login *************
    @SuppressLint("SetJavaScriptEnabled")
    fun setupAppleWebviewDialog(url: String) {
        val webView = WebView(requireContext())
        appledialog = Dialog(requireContext())

        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = AppleWebViewClient()

        webView.settings.javaScriptEnabled = true

        webView.loadUrl(url)

        appledialog.setContentView(webView)
        appledialog.show()
        appledialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)


        val parent = FrameLayout(requireContext())
        val parentParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        parentParams.gravity = Gravity.CENTER

        dialogProgressbar = ProgressBar(requireContext(), null, R.attr.progressBarStyle)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        dialogProgressbar.textAlignment = View.TEXT_ALIGNMENT_CENTER
        parent.addView(dialogProgressbar, layoutParams)

        webView.addView(parent, parentParams)
        dialogProgressbar.visibility = View.VISIBLE

        binding.spinner.visibility = View.GONE
    }

    // A client to know about WebView navigation
    // For API 21 and above
    @Suppress("OverridingDeprecatedMember")
    inner class AppleWebViewClient : WebViewClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request?.url.toString().contains("willow.tv/apple_device_login")) {
                extractParamsFromGetRequest(request!!.url.toString())
                return true
            }
            return true
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains("willow.tv/apple_device_login")) {
                extractParamsFromGetRequest(url)
                return true
            }
            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            dialogProgressbar.visibility = View.GONE
        }

        private fun extractParamsFromGetRequest(url: String) {
            UserModel.setAppleLoginResponseData(url)
            ReloadService.reloadAllScreens()
            activity?.finish()
        }
    }


    private fun requestForAccessToken(code: String, clientSecret: String) {
        val grantType = "authorization_code"

        val postParamsForAuth =
            "grant_type=" + grantType + "&code=" + code + "&redirect_uri=" + AppleConstants.REDIRECT_URI + "&client_id=" + AppleConstants.CLIENT_ID + "&client_secret=" + clientSecret

        CoroutineScope(Dispatchers.Default).launch {
            val httpsURLConnection =
                withContext(Dispatchers.IO) { URL(AppleConstants.TOKENURL).openConnection() as HttpsURLConnection }
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            withContext(Dispatchers.IO) {
                val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                outputStreamWriter.write(postParamsForAuth)
                outputStreamWriter.flush()
            }
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8

            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            val accessToken = jsonObject.getString("access_token") // Here is the access token
            Log.i("Apple Access Token is: ", accessToken)

            val expiresIn = jsonObject.getInt("expires_in") // When the access token expires
            Log.i("expires in: ", expiresIn.toString())

            val refreshToken = jsonObject.getString("refresh_token") // The refresh token used to regenerate new access tokens. Store context token securely on your server.
            Log.i("refresh token: ", refreshToken)

            val idToken = jsonObject.getString("id_token") // A JSON Web Token that contains the user’s identity information.
            Log.i("ID Token: ", idToken)

            // Get encoded user id by splitting idToken and taking the 2nd piece
            val encodedUserID = idToken.split(".")[1]

            // Decode encoded UserID to JSON

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val decodedUserData = Base64.getDecoder()
                    .decode(encodedUserID) //String(Base64.decode(encodedUserID, Base64.DEFAULT))
                val userDataJsonObject = JSONObject(decodedUserData.toString())


                // Get User's ID
                val userId = userDataJsonObject.getString("sub")
                Log.i("Apple User ID :", userId)
                appleId = userId
            }
        }
    }


    private suspend fun verifyRefreshToken(): Boolean {
        // Verify Refresh Token only once a day
        val sharedPref = getActivity()?.getPreferences(Context.MODE_PRIVATE)
        val refreshToken = sharedPref?.getString("refresh_token", "")
        val clientSecret = sharedPref?.getString("client_secret", "")

        val postParamsForAuth =
            "grant_type=refresh_token" + "&client_id=" + AppleConstants.CLIENT_ID + "&client_secret=" + clientSecret + "&refresh_token=" + refreshToken

        val httpsURLConnection = withContext(Dispatchers.IO) { URL(AppleConstants.TOKENURL).openConnection() as HttpsURLConnection }
        httpsURLConnection.requestMethod = "POST"
        httpsURLConnection.setRequestProperty(
            "Content-Type",
            "application/x-www-form-urlencoded"
        )
        httpsURLConnection.doInput = true
        httpsURLConnection.doOutput = true
        withContext(Dispatchers.IO) {
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            outputStreamWriter.write(postParamsForAuth)
            outputStreamWriter.flush()
        }
        try {
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            val newAccessToken = jsonObject.getString("access_token")
            //Replace the Access Token on your server with the new one
            Log.d("New Access Token: ", newAccessToken)

            appleAccessToken = newAccessToken
            return true
        } catch (e: Exception) {
            Log.e("ERROR: ", "Refresh Token has expired or user revoked app credentials")
            return false
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val sharedPref = getActivity()?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            val expireTime = sharedPref.getLong("verify_refresh_token_timer", 0)

            val currentTime = System.currentTimeMillis() / 1000L // Check the current Unix Time

            if (currentTime >= expireTime) {
                // After 24 hours validate the Refresh Token and generate new Access Token
                val untilUnixTime =
                    currentTime + (60 * 60 * 24) // Execute the method after 24 hours again
                sharedPref.edit().putLong("verify_refresh_token_timer", untilUnixTime).apply()
                return verifyRefreshToken()
            } else {
                return true
            }
        }

        return false
    }

    fun sendAnalyticsEvent() {
         AnalyticsService.trackFirebaseScreen("AN_LOGIN_PAGE")
    }

    fun showMessage(message: String) {
        if (message.isEmpty()) { return }

        val intent = Intent(context, MessagePopupActivity::class.java).apply {}
        intent.putExtra("MESSAGE", message)
        startActivity(intent)
    }
}

object AppleConstants {
    val CLIENT_ID = "tv.willow.login.web.apple"
    val REDIRECT_URI = "https://www.willow.tv/apple_web_login"
    val SCOPE = "name%20email"
    val AUTHURL = "https://appleid.apple.com/auth/authorize"
    val TOKENURL = "https://appleid.apple.com/auth/token"
}