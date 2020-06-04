package com.thuypham.ptithcm.mytiki.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thuypham.ptithcm.mytiki.data.*
import com.thuypham.ptithcm.mytiki.repository.ProductRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    var product = MutableLiveData<Product>().apply { value = Product() }
    var category = MutableLiveData<Category>().apply { value = Category() }

    /* get list product of category */
    private val listProductResponse = MutableLiveData<ResultData<ArrayList<Product>>>()
    val listProduct = Transformations.switchMap(listProductResponse) {
        it.data
    }

    val networkListProduct = Transformations.switchMap(listProductResponse) {
        it.networkState
    }

    fun getAllProductOfCategory(categoryID: String) {
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

    fun getAllProduct() {
        responeAllProducts.value = repository.getAllProducts()
    }

    /* get all product search*/
    private val responseProductSearch = MutableLiveData<ResultData<ArrayList<Product>>>()

    val listProductSearch = Transformations.switchMap(responseProductSearch) {
        it.data
    }

    val networkProductSearch = Transformations.switchMap(responseProductSearch) {
        it.networkState
    }

    fun getProductSearch(keySearch: String) {
        responseProductSearch.value = repository.searchProduct(keySearch)
    }

    /* get all product sale */
    private val responeAllProductsSale = MutableLiveData<ResultData<ArrayList<Product>>>()

    val listAllProductsSale = Transformations.switchMap(responeAllProductsSale) {
        it.data
    }

    val networkAllProductsSale = Transformations.switchMap(responeAllProductsSale) {
        it.networkState
    }

    fun getAllProductSale(limit: Int? = 10) {
        responeAllProductsSale.value = repository.getAllProductsSale(limit)
    }

    /* get all product sale of category*/
    private val responseAllProductsSaleOfCate = MutableLiveData<ResultData<List<Product>>>()

    val listAllProductsSaleOfCate = Transformations.switchMap(responseAllProductsSaleOfCate) {
        it.data
    }

    val networkAllProductsSaleOfCate = Transformations.switchMap(responseAllProductsSaleOfCate) {
        it.networkState
    }

    fun getAllProductSaleOfCate(categoryID: String, limit: Int? = 10) {
        responseAllProductsSaleOfCate.value = repository.getProductSaleOfCategory(categoryID, limit)
    }

    /* get all product sold of category*/
    private val responseAllProductsSoldOfCate = MutableLiveData<ResultData<List<Product>>>()

    val listAllProductsSoldOfCate = Transformations.switchMap(responseAllProductsSoldOfCate) {
        it.data
    }

    val networkAllProductsSoldOfCate = Transformations.switchMap(responseAllProductsSoldOfCate) {
        it.networkState
    }

    fun getAllProductSoldOfCate(categoryID: String, limit: Int? = 10) {
        responseAllProductsSoldOfCate.value = repository.getProductSoldOfCategory(categoryID, limit)
    }


    /* get cart count*/
    private val responeAllPCartCount = MutableLiveData<ResultData<Int>>()

    val cartCount = Transformations.switchMap(responeAllPCartCount) {
        it.data
    }

    val networkCartCount = Transformations.switchMap(responeAllPCartCount) {
        it.networkState
    }

    fun getCartCount() {
        responeAllPCartCount.value = repository.getCartCount()
    }

    /* get productId viewed*/
    private val responeAllProductIDViewed = MutableLiveData<ResultData<ArrayList<String>>>()

    val listIdProductViewed = Transformations.switchMap(responeAllProductIDViewed) {
        it.data
    }

    fun getListProductViewed() {
        responeAllProductIDViewed.value = repository.getListIdProductViewed()
    }

    /* add product */
    private var responseAddProduct = MutableLiveData<ResultData<Product>>()

    val networkAddProduct = Transformations.switchMap(responseAddProduct) {
        it.networkState
    }

    val productAdd = Transformations.switchMap(responseAddProduct) {
        it.data
    }

    fun addProduct(product: Product, imageUri: Uri?) {
        responseAddProduct.value = repository.addProduct(product, imageUri)
    }

    /* get info of product by id */
    private var responseProductByID = MutableLiveData<ResultData<Product>>()

    val productByID = Transformations.switchMap(responseProductByID) {
        it.data
    }

    val netWorkProductId = Transformations.switchMap(responseProductByID) {
        it.networkState
    }

    fun getProductByID(productId: String) {
        responseProductByID.value = repository.getProductByID(productId)
    }

    /* update product*/
    private var responseUpdateProduct = MutableLiveData<NetworkState>()

    fun updateProduct(product: Product) {
        responseUpdateProduct = repository.updateProduct(product)
    }

    /* Del product*/
    private var responseDelProduct = MutableLiveData<NetworkState>()

    fun delProduct(productId: String) {
        responseDelProduct = repository.delProduct(productId)
    }

    /* Del product*/
    private var responseSendNotification = MutableLiveData<JSONObject>()

    fun sendNotificationAddNewProduct(product: ProductAdd) {
        viewModelScope.launch {
          repository.sendNotification(product)
        }
    }
}