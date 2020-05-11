package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.CategoryRepository

class CategoryViewModel(private val repository:CategoryRepository) :ViewModel(){

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
    private var responseAddCategory = MutableLiveData<NetworkState>()

    fun addCategory(category: Category){
        responseAddCategory = repository.addCategory(category)
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