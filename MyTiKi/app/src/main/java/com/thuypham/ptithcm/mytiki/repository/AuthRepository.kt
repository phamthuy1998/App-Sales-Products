package com.thuypham.ptithcm.mytiki.repository

import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User

interface AuthRepository {
    /* for customer */
    fun login(email: String, password: String): ResultData<User>
    fun logOut(): ResultData<Boolean>
    fun updateUserInfo(user:User): ResultData<Boolean>
    fun sendMailResetPassword(email: String): ResultData<Boolean>
    fun register(user: User): ResultData<Boolean>
    fun getAccInfo(email: String): ResultData<User>
    fun getCurrentUser(): ResultData<User>

}