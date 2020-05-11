package com.thuypham.ptithcm.mytiki.repository

import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User

interface AccountRepository {
    /* For admin */
    fun getAllEmployee(): ResultData<ArrayList<User>>
    fun getEmployeeByRole(role: Long): ResultData<ArrayList<User>>
    fun createAcc(user: User): MutableLiveData<NetworkState>
    fun updateAccount(user: User): MutableLiveData<NetworkState>
    fun deleteAcc(user: User): MutableLiveData<NetworkState>
}