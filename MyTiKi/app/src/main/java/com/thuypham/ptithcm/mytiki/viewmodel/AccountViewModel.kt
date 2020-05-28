package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AccountRepository

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    val user = MutableLiveData<User>().apply { value = User() }

    var phone = MutableLiveData<String>().apply { value = "" }
    var name = MutableLiveData<String>().apply { value = "" }
    var email = MutableLiveData<String>().apply { value = "" }
    var password = MutableLiveData<String>().apply { value = "" }
    var dayCreateAcc = MutableLiveData<String>().apply { value = "" }
    var birthday = MutableLiveData<String>().apply { value = "" }

    fun setUser(user: User) {
        phone.value = user.phone
        name.value = user.name
        email.value = user.email
        password.value = user.password
        birthday.value = user.birthday
    }

    fun setUserUpdate() {
        user.value.also {
            it?.phone = phone.value
            it?.name = name.value
            it?.email = email.value
            it?.password = password.value
            it?.birthday = birthday.value
        }
    }


    /* get all acc */
    private val responseListAccount = MutableLiveData<ResultData<ArrayList<User>>>()
    val listAccount = Transformations.switchMap(responseListAccount) {
        it.data
    }

    val networkAcc = Transformations.switchMap(responseListAccount) {
        it.networkState
    }

    fun getAccInRole(role: Long) {
        responseListAccount.value = repository.getEmployeeByRole(role)
    }

    fun getAllAccount() {
        responseListAccount.value = repository.getAllEmployee()
    }

    /* create account */
    private var responseCreateAcc = MutableLiveData<ResultData<User>>()

    val userCreated = Transformations.switchMap(responseCreateAcc) {
        it.data
    }

    val networkCreateAcc = Transformations.switchMap(responseCreateAcc) {
        it.networkState
    }

    fun createAcc(user: User) {
        responseCreateAcc.value = repository.createAcc(user)
    }

    /* update account*/
    private var responseUpdateAccount = MutableLiveData<ResultData<User>>()

    val userUpdated = Transformations.switchMap(responseUpdateAccount) {
        it.data
    }

    val networkUpdateAcc = Transformations.switchMap(responseUpdateAccount) {
        it.networkState
    }

    fun updateAcc(user: User) {
        responseUpdateAccount.value = repository.updateAccount(user)
    }

    /* Del account*/
    var responseDelAccount = MutableLiveData<ResultData<Boolean>>()

    val networkDelAcc = Transformations.switchMap(responseDelAccount) {
        it.networkState
    }

    fun delAccount(user: User) {
        responseDelAccount.value = repository.deleteAcc(user)
    }
}