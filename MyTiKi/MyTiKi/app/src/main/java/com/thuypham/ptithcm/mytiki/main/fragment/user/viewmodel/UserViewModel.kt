package com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel

import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.*
import io.reactivex.disposables.CompositeDisposable

class UserViewModel(private var status_login: Int = 0) : ViewModel(), LifecycleObserver {

    companion object {
        const val STATUS_LOGIN = "Status_login"
    }

    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var pasword: String = ""
    var dayCreateAcc: String = ""

    private val compositeDisposable by lazy { CompositeDisposable() }
    private var _isLogin = MutableLiveData<Boolean>().apply { value = false }
    private val _loading = MutableLiveData<Boolean>().apply { value = false }

    val isLoading: LiveData<Boolean>
        get() = _loading


    val isLogin: LiveData<Boolean>
        get() = _isLogin

    fun logInFireBase() {
        _isLogin.value = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        logInFireBase()
    }

    fun saveState(outState: Bundle) {
        outState.putInt(STATUS_LOGIN, status_login)
    }

    fun restoreState(inState: Bundle?) {
        inState?.let { status_login = inState.getInt(STATUS_LOGIN) }
    }

    private fun notLoggedIn() {
        _isLogin.value = false
    }

    fun afterNameChange(tx: Editable) {
        name = tx.toString()
    }

    fun afterPhoneChange(tx: Editable) {
        phone = tx.toString()
    }

    fun afterEmailChange(charSequence: Editable) {
        email = charSequence.toString()
    }

}