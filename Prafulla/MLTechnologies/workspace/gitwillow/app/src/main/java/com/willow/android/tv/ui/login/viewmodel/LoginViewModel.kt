package com.willow.android.tv.ui.login.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.willow.android.tv.data.repositories.RepositoryFactory
import com.willow.android.tv.data.repositories.loginpage.datamodel.APICheckAccountDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APIForgotPassDataModel
import com.willow.android.tv.data.repositories.loginpage.datamodel.APILoginDataModel
import com.willow.android.tv.data.repositories.signuppage.datamodel.APISIgnupDataModel
import com.willow.android.tv.ui.login.model.LoginResultModel
import com.willow.android.tv.ui.login.model.LoginUserModel
import com.willow.android.tv.ui.login.model.SignUpUserModel
import com.willow.android.tv.utils.Actions
import com.willow.android.tv.utils.CommonFunctions
import com.willow.android.tv.utils.PrefRepository
import com.willow.android.tv.utils.Resource
import com.willow.android.tv.utils.SingleLiveEvent
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var loginOrRegister = MutableLiveData<String>()


    private val _renderPage = MutableLiveData<Resource<APILoginDataModel>>()
    val renderPage: LiveData<Resource<APILoginDataModel>> = _renderPage

    private val _renderPageSignup = MutableLiveData<Resource<APISIgnupDataModel>>()
    val renderPageSignup: LiveData<Resource<APISIgnupDataModel>> = _renderPageSignup

    private val _renderPageCheckAcc = MutableLiveData<Resource<APICheckAccountDataModel>>()
    val renderPageCheckAcc: LiveData<Resource<APICheckAccountDataModel>> = _renderPageCheckAcc

    private val _renderPageForgotPass = SingleLiveEvent<Resource<APIForgotPassDataModel>>()
    val renderPageForgotPass: LiveData<Resource<APIForgotPassDataModel>> = _renderPageForgotPass

    var userSignupMutableLiveData: MutableLiveData<SignUpUserModel?>? = null
    private var _loginResult =  MutableLiveData<LoginResultModel?>()
    val loginResult: MutableLiveData<LoginResultModel ?> = _loginResult



    val prefRepository = PrefRepository(application)



    fun getSignupUser(): MutableLiveData<SignUpUserModel?>? {
        if (userSignupMutableLiveData == null) {
            userSignupMutableLiveData = MutableLiveData()
        }
        return userSignupMutableLiveData
    }
    fun loginStatus(): MutableLiveData<LoginResultModel?> {
        return _loginResult
    }

    fun onClick(view: View?) {
        if(email.value!=null && password.value!=null&& loginOrRegister.value!=null){
            _renderPage.postValue(Resource.Loading())

            val loginUser = LoginUserModel(loginOrRegister.value!!,email.value!!, password.value!!,
                CommonFunctions.generateMD5Common(email.value + "::"+password.value))
            val data =  RepositoryFactory.getLoginPageRepository().getLoginPageData(getApplication())
            viewModelScope.launch {
                if (loginOrRegister.value==Actions.LOGIN.action){
                    _renderPage.postValue(data.getLoginPage(loginUser))
                }
                else if(loginOrRegister.value==Actions.REGISTER.action){
                    _renderPage.postValue(data.getSignupUser(loginUser))
                }
            }
        }else{
            _loginResult.postValue(LoginResultModel(false, "Please input credentials", (email.value==null),password.value==null))

        }
    }

    fun checkEmailExists(email:String) {
        if(email!=null ){
            _renderPage.postValue(Resource.Loading())
            val loginUser = LoginUserModel(Actions.CHECK_ACCOUNT.action,email,null,
                CommonFunctions.generateMD5Common(email))
            val data =  RepositoryFactory.getLoginPageRepository().getLoginPageData(getApplication())
            viewModelScope.launch {

                _renderPageCheckAcc.postValue(data.getCheckAccount(loginUser))

            }
        }
    }


    fun onClickRegister(view: View?) {

        val signUpUser = SignUpUserModel(Actions.REGISTER.action,name.value!!,email.value!!, password.value!!,
            CommonFunctions.generateMD5Common(email.value!!+"::"+password.value!!))
        userSignupMutableLiveData!!.value = signUpUser
        val data =  RepositoryFactory.getSignUpPageRepository().getSignUpPageData(getApplication())
        viewModelScope.launch {

//            bindSignUpAPIDataToPageModel(data.getSignupUser(signUpUser).value)
            _renderPageSignup.postValue(data.getSignupUser(signUpUser))

        }

    }

    fun onClickForgotPassword(email:String) {

        if(email!=null ){
            _renderPage.postValue(Resource.Loading())
            val loginUser = LoginUserModel(Actions.FG_PASSWORD.action,email,null,
                CommonFunctions.generateMD5Common(email))
            val data =  RepositoryFactory.getLoginPageRepository().getLoginPageData(getApplication())
            viewModelScope.launch {

                _renderPageForgotPass.postValue(data.getForgotPassword(loginUser))

            }
        }

    }

    fun bindAPIDataToPageModel(data: APILoginDataModel?) {

        data?.apply {
            if(result.status =="success"){
                prefRepository.apply {
                    setLoggedIn(true)
                    setUserData(data)
                    setUserID(data.result.userId.toString())
                    setUserSubscribed(data.result.subscriptionStatus==1)
                }
                _loginResult.postValue(LoginResultModel(true,"success"))
            }else{
                _loginResult.postValue(LoginResultModel(false,result.message.toString()))
            }
        }
    }

    fun bindSignUpAPIDataToPageModel(data: APISIgnupDataModel?) {

        data?.apply {
            if(result.status =="success"){
                _loginResult.postValue(LoginResultModel(true,"success"))
            }else{
                _loginResult.postValue(LoginResultModel(false,result.message.toString()))            }
        }
    }

    fun clearLiveData(){
        email.value =""
        name.value= ""
        password.value=""
        loginResult.value =null
    }
}