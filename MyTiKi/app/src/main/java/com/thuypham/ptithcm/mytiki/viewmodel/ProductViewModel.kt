package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.ProductRepository

class ProductViewModel(private val repository:ProductRepository) :ViewModel(){

    /* get list product of category */
    private val listProductResponse = MutableLiveData<ResultData<ArrayList<Product>>>()
    val listProduct = Transformations.switchMap(listProductResponse) {
        it.data
    }

    val networkListProduct = Transformations.switchMap(listProductResponse) {
        it.networkState
    }

    fun getAllProductOfCategory(categoryID: String){
        listProductResponse.value = repository.getAllProductOfCategory(categoryID)
    }

    /* get all product*/
    private val responeAllProducts = MutableLiveData<ResultData<ArrayList<Product>>>()

    val listAllProducts = Transformations.switchMap(responeAllProducts) {
        it.data
    }

    val networkAllProducts = Transformations.switchMap(responeAllProducts) {
        it.networkState
    }

    fun getAllProduct(){
        responeAllProducts.value = repository.getAllProducts()
    }

    /* add product */
    private var responseAddProduct = MutableLiveData<NetworkState>()

    fun addProduct(product: Product){
        responseAddProduct = repository.addProduct(product)
    }

    /* get info of product by id */
    private var responseProductByID = MutableLiveData<ResultData<Product>>()

    val productByID = Transformations.switchMap(responseProductByID) {
        it.data
    }

    val netWorkProductId = Transformations.switchMap(responseProductByID) {
        it.networkState
    }

    fun getProductByID(productId: String){
        responseProductByID.value = repository.getProductByID(productId)
    }

    /* update product*/
    private var responseUpdateProduct = MutableLiveData<NetworkState>()

    fun updateProduct(product: Product){
        responseUpdateProduct = repository.updateProduct(product)
    }

    /* Del product*/
    private var responseDelProduct = MutableLiveData<NetworkState>()

    fun delProduct(productId: String){
        responseDelProduct = repository.delProduct(productId)
    }
}