package com.thuypham.ptithcm.mytiki.viewmodel

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AuthRepository
import kotlinx.coroutines.async

class UserViewModel(private val repository: AuthRepository) : ViewModel() {

    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var pasword: String = ""
    var dayCreateAcc: String = ""

    fun afterEmailChange(charSequence: Editable) {
        email = charSequence.toString()
    }

    val responseLogin = MutableLiveData<ResultData<User>>()

    fun login(email: String, password: String) {
        viewModelScope.async {

        }
    }
}