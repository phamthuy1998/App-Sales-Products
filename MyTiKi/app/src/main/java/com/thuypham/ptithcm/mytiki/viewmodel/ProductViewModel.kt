package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.repository.ProductRepository

class ProductViewModel(private val repository:ProductRepository) :ViewModel(){


    val productReponse = MutableLiveData<PagedList<Product>>()

    fun getAllProductOfCategory(categoryID: String){
//        productReponse.value = repository.getAllProductOfCategory(categoryID)
    }
}