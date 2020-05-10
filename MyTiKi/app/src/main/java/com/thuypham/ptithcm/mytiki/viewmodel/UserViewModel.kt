package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AuthRepository


class UserViewModel(private val repository: AuthRepository) : ViewModel() {

    var phone = MutableLiveData<String>().apply { value = "" }
    var name = MutableLiveData<String>().apply { value = "" }
    var email = MutableLiveData<String>().apply { value = "" }
    var password = MutableLiveData<String>().apply { value = "" }
    var dayCreateAcc = MutableLiveData<String>().apply { value = "" }
    var birthday = MutableLiveData<String>().apply { value = "" }

    fun isValidateSignUp(): Boolean =
        phone.value?.isNotEmpty()!! && name.value?.isNotEmpty()!! && email.value?.isNotEmpty()!! && password.value?.isNotEmpty()!! && birthday.value?.isNotEmpty()!!

    private val responseLogin = MutableLiveData<ResultData<User>>()
    private val responseUserInfo = MutableLiveData<ResultData<User>>()
    private val responseRegister = MutableLiveData<ResultData<Boolean>>()
    private val responseForgotPW = MutableLiveData<ResultData<Boolean>>()

    /* user login */
    val userInfoLogin = Transformations.switchMap(responseLogin) {
        it.data
    }

    val networkStateUserLogin = Transformations.switchMap(responseLogin) {
        it.networkState
    }

    fun login(email: String, password: String) {
        responseLogin.value = repository.login(email, password)
    }

    /*  User's info */
    val userInfo = Transformations.switchMap(responseUserInfo) {
        it.data
    }

    val networkStateUserInfo = Transformations.switchMap(responseUserInfo) {
        it.networkState
    }

    fun getUserInfoByEmail(email: String) {
        responseUserInfo.value = repository.getAccInfo(email)
    }

    /*    Register  */
    val isRegister = Transformations.switchMap(responseRegister) {
        it.data
    }

    val networkRegister = Transformations.switchMap(responseRegister) {
        it.networkState
    }

    fun register(user: User) {
        responseRegister.value = repository.register(user)
    }

    /*    Forgot password  */
    val networkSendMail = Transformations.switchMap(responseForgotPW) {
        it.networkState
    }

    fun resetPassword(email: String) {
        responseForgotPW.value = repository.sendMailResetPassword(email)
    }
}