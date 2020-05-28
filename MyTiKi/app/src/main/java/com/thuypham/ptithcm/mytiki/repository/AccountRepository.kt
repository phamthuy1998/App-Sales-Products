package com.thuypham.ptithcm.mytiki.repository

import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User

interface AccountRepository {
    /* For admin */
    fun getAllEmployee(): ResultData<ArrayList<User>>
    fun getEmployeeByRole(role: Long): ResultData<ArrayList<User>>
    fun createAcc(user: User): ResultData<User>
    fun updateAccount(user: User): ResultData<User>
    fun deleteAcc(user: User): ResultData<Boolean>
    fun getAllRoleLogin(): ResultData<NetworkState>
}