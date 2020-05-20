package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AccountRepository

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    val user = MutableLiveData<User>().apply { value = User() }
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
    private var responseCreateAcc = MutableLiveData<NetworkState>()

    fun createAcc(user: User) {
        responseCreateAcc = repository.createAcc(user)
    }

    /* update account*/
    private var responseUpdateAccount = MutableLiveData<NetworkState>()

    fun updateAcc(user: User) {
        responseUpdateAccount = repository.updateAccount(user)
    }

    /* Del account*/
    private var responseDelAccount = MutableLiveData<NetworkState>()

    fun delAccount(user: User) {
        responseDelAccount = repository.deleteAcc(user)
    }
}