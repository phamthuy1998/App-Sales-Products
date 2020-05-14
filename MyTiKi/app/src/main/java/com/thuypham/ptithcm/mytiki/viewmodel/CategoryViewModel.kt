package com.thuypham.ptithcm.mytiki.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.CategoryRepository

class CategoryViewModel(private val repository:CategoryRepository) :ViewModel(){

    var name = MutableLiveData<String>().apply { value = "" }
    var category = MutableLiveData<Category>().apply { value = Category() }

    /* get all list category */
    private val responseListCategory = MutableLiveData<ResultData<ArrayList<Category>>>()
    val listCategories = Transformations.switchMap(responseListCategory) {
        it.data
    }

    val networkListCategory = Transformations.switchMap(responseListCategory) {
        it.networkState
    }

    fun getAllCategory(){
        responseListCategory.value = repository.getAllCategory()
    }

    /* add category */
    var responseAddCategory = MutableLiveData<ResultData<Category>>()

    val networkAddCategory = Transformations.switchMap(responseAddCategory) {
        it.networkState
    }

    val categoryAdd = Transformations.switchMap(responseAddCategory) {
        it.data
    }

    fun addCategory(category: Category, imageUri: Uri?=null){
        responseAddCategory.value = repository.addCategory(category,imageUri)
    }

    /*  update category */
    private var responseUpdateCategory = MutableLiveData<NetworkState>()

    fun updateCategory(category: Category){
        responseUpdateCategory = repository.updateCategory(category)
    }

    /* Del category*/
    private var responseDelCategory = MutableLiveData<NetworkState>()

    fun delCategory(categoryID: String){
        responseDelCategory = repository.delCategory(categoryID)
    }
}