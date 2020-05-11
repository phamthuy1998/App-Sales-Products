package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.ProductRepository
import com.thuypham.ptithcm.mytiki.util.Constant.ID_CATEGORY_PRODUCT
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT_DEL

class ProductRepositoryImpl : ProductRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun databaseRef() = firebaseDatabase?.reference

    override fun getAllProductOfCategory(categoryId: String): ResultData<ArrayList<Product>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<ArrayList<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query = databaseRef()?.child(PRODUCT)?.orderByChild(ID_CATEGORY_PRODUCT)
            ?.equalTo(categoryId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        product?.let { listProduct.add(it) }
                    }
                    responseListProduct.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List product are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListProduct,
            networkState = networkState
        )
    }

    override fun getAllProducts(): ResultData<ArrayList<Product>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<ArrayList<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query = databaseRef()?.child(PRODUCT)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        product?.let { listProduct.add(it) }
                    }
                    responseListProduct.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List product are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListProduct,
            networkState = networkState
        )
    }

    override fun addProduct(product: Product): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        val idPush = databaseRef()?.child(PRODUCT)?.push()?.key
        product.id = idPush
        databaseRef()?.child(PRODUCT)?.child(idPush.toString())?.setValue(product)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun getProductByID(productID: String): ResultData<Product> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<Product>()
        networkState.postValue(NetworkState.LOADING)
        var product: Product?
        val query = databaseRef()?.child(PRODUCT)?.child(productID)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    product = dataSnapshot.getValue(Product::class.java)
                    responseListProduct.value = product
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("Cant't load this product's info!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListProduct,
            networkState = networkState
        )
    }

    override fun updateProduct(product: Product): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(PRODUCT)?.child(product.id.toString())?.setValue(product)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun delProduct(productID: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(PRODUCT)?.child(productID)?.child(PRODUCT_DEL)?.setValue(true)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

}