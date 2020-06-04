package com.thuypham.ptithcm.mytiki.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.ProductAdd
import com.thuypham.ptithcm.mytiki.data.ResultData

interface ProductRepository {
    fun getAllProductOfCategory(categoryId: String): ResultData<ArrayList<Product>>
    fun getAllProducts(): ResultData<ArrayList<Product>>
    fun searchProduct(keySearch: String): ResultData<ArrayList<Product>>
    fun getAllProductsSale(limit: Int? = 10): ResultData<ArrayList<Product>>
    fun getProductSaleOfCategory(category: String, limit:Int?=10): ResultData<List<Product>>
    fun getProductSoldOfCategory(category: String, limit:Int?=10): ResultData<List<Product>>
    fun getListIdProductViewed(): ResultData<ArrayList<String>>
    fun getCartCount(): ResultData<Int>
    fun addProduct(product: Product, imageUri: Uri? = null): ResultData<Product>
    fun getProductByID(productID: String): ResultData<Product>
    fun updateProduct(product: Product): MutableLiveData<NetworkState>
    fun delProduct(productID: String): MutableLiveData<NetworkState>

    suspend fun sendNotification(product: ProductAdd)
}