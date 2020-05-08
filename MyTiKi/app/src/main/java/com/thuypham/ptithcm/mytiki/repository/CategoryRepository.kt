package com.thuypham.ptithcm.mytiki.repository

import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData

interface CategoryRepository {
    fun getAllCategory(): ResultData<ArrayList<Category>>
    fun addCategory(category: Category): MutableLiveData<NetworkState>
    fun updateCategory(category: Category): MutableLiveData<NetworkState>
    fun delCategory(categoryID: String): MutableLiveData<NetworkState>
}