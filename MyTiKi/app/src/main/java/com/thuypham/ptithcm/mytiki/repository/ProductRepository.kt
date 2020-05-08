package com.thuypham.ptithcm.mytiki.repository

import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.ResultData

interface ProductRepository {
    fun getAllProductOfCategory(categoryId: String): ResultData<ArrayList<Product>>
    fun addProduct(product: Product): MutableLiveData<NetworkState>
    fun getProductByID(productID: String):  ResultData<Product>
    fun updateProduct(product: Product): MutableLiveData<NetworkState>
    fun delProduct(productID: String): MutableLiveData<NetworkState>
}