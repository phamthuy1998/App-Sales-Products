package com.thuypham.ptithcm.mytiki.repository

import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User

interface AuthRepository {
    /* for customer */
    fun login(email: String, password: String): ResultData<User>
    fun updateUserInfo(user:User): ResultData<Boolean>
    fun sendMailResetPassword(email: String): ResultData<Boolean>
    fun register(user: User): ResultData<Boolean>
    fun getAccInfo(email: String): ResultData<User>

    /* For admin */
    fun getAllEmployee(): ResultData<ArrayList<User>>
    fun deleteAcc(user: User): MutableLiveData<NetworkState>
}