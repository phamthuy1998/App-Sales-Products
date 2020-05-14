package com.thuypham.ptithcm.mytiki.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData

interface CategoryRepository {
    fun getAllCategory(): ResultData<ArrayList<Category>>
    fun addCategory(category: Category,imageUri: Uri?=null): ResultData<Category>
    fun updateCategory(category: Category): MutableLiveData<NetworkState>
    fun delCategory(categoryID: String): MutableLiveData<NetworkState>
}