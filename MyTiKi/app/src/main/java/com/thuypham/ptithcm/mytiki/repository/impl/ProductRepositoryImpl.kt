package com.thuypham.ptithcm.mytiki.repository.impl

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.ProductAdd
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.ProductRepository
import com.thuypham.ptithcm.mytiki.services.NotificationApi
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.Constant.ID_CATEGORY_PRODUCT
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT_DEL
import com.thuypham.ptithcm.mytiki.util.Constant.PRODUCT_SALE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class ProductRepositoryImpl(private val apiService: NotificationApi) : ProductRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }
    private val fireStore: FirebaseStorage? by lazy {
        Firebase.storage
    }
    private val firebaseAuth: FirebaseAuth? by lazy {
        FirebaseAuth.getInstance()
    }

    private fun storeRef() = fireStore?.reference
    private fun databaseRef() = firebaseDatabase?.reference
    private fun currentUser() = firebaseAuth?.currentUser
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
                        if (product?.del == false)
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

    @SuppressLint("DefaultLocale")
    private fun removeAccent(s: String?): String {
        val str = s?.toLowerCase()
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
    }

    override fun searchProduct(keySearch: String): ResultData<ArrayList<Product>> {
        val search = removeAccent(keySearch)
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<ArrayList<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query =
            databaseRef()?.child(PRODUCT)//?.orderByChild(NAME_PRODUCT)?.startAt(keySearch)?.endAt(keySearch + "\uf8ff")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        if (product?.del == false)
                            if (removeAccent(product?.name).contains(search)
                                || product?.price.toString().contains(search)
                                || removeAccent(product?.infor).contains(search)
                            )
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

    override fun getAllProductsSale(limit: Int?): ResultData<ArrayList<Product>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<ArrayList<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query = if (limit != 10) databaseRef()?.child(PRODUCT)?.orderByChild(PRODUCT_SALE)
        else databaseRef()?.child(PRODUCT)?.orderByChild(PRODUCT_SALE)?.limitToLast(10)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        if (product?.del == false && product?.sale != 0L)
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

    override fun getProductSaleOfCategory(
        category: String,
        limit: Int?
    ): ResultData<List<Product>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<List<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query = databaseRef()?.child(PRODUCT)
            ?.orderByChild(ID_CATEGORY_PRODUCT)
            ?.equalTo(category)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        if (product?.del == false && product?.sale != 0L)
                            product?.let { listProduct.add(it) }
                    }

                    val sortedList = listProduct.sortedByDescending(Product::sale)
                    responseListProduct.value = sortedList
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

    override fun getProductSoldOfCategory(
        category: String,
        limit: Int?
    ): ResultData<List<Product>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<List<Product>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Product>()
        var product: Product?
        val query = databaseRef()?.child(PRODUCT)
            ?.orderByChild(ID_CATEGORY_PRODUCT)?.equalTo(category)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        product = ds.getValue(Product::class.java)
                        if (product?.del == false && product?.sold != 0L)
                            product?.let { listProduct.add(it) }
                    }
                    val sortedList = listProduct.sortedByDescending(Product::sold)
                    responseListProduct.value = sortedList
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

    override fun getListIdProductViewed(): ResultData<ArrayList<String>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProductID = MutableLiveData<ArrayList<String>>()
        networkState.postValue(NetworkState.LOADING)
        val listProductID = ArrayList<String>()
        val query = databaseRef()?.child(Constant.USER)?.child(currentUser()?.uid.toString())
            ?.child(Constant.VIEWED_PRODUCT)
            ?.limitToLast(10)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val id: String? = ds.child(Constant.VIEWED_PRODUCT_ID).value as String?
                        id?.let { listProductID.add(it) }
                    }
                    responseListProductID.value = listProductID
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
            data = responseListProductID,
            networkState = networkState
        )
    }

    override fun getCartCount(): ResultData<Int> {
        val networkState = MutableLiveData<NetworkState>()
        val response = MutableLiveData<Int>()
        networkState.postValue(NetworkState.LOADING)
        var cartCount: Int
        val query = databaseRef()?.child(Constant.CART)?.child(currentUser()?.uid.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    cartCount = 0
                    for (ds in dataSnapshot.children) {
                        if (ds.exists()) {
                            cartCount++
                        }
                    }
                    response.value = cartCount
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
            data = response,
            networkState = networkState
        )
    }

    override fun addProduct(product: Product, imageUri: Uri?): ResultData<Product> {
        val responseSlide = MutableLiveData<Product>()
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING


//        val idPush = databaseRef()?.child(PRODUCT)?.push()?.key
//        product.id = idPush
//        databaseRef()?.child(PRODUCT)?.child(idPush.toString())?.setValue(product)
//            ?.addOnCompleteListener {
//                networkState.value = NetworkState.LOADED
//            }
//            ?.addOnFailureListener { err ->
//                networkState.postValue(NetworkState.error(err.message))
//            }
//        return networkState

        if (imageUri == null) {
            if (product.id == null) {
                val idPush = databaseRef()?.child(PRODUCT)?.push()?.key
                product.id = idPush
            }
            databaseRef()?.child(PRODUCT)?.child(product.id.toString())?.setValue(product)
                ?.addOnCompleteListener {
                    networkState.value = NetworkState.LOADED
                    responseSlide.value = product
                }
                ?.addOnFailureListener { err ->
                    networkState.postValue(NetworkState.error(err.message))
                }
        } else {
            val ref = storeRef()?.child(PRODUCT)?.child(UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(imageUri)

            uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        networkState.value = NetworkState.error(it.message)
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (product.id == null) {
                        val idPush = databaseRef()?.child(PRODUCT)?.push()?.key
                        product.id = idPush
                    }
                    product.image = downloadUri.toString()
                    databaseRef()?.child(PRODUCT)?.child(product.id.toString())?.setValue(product)
                        ?.addOnCompleteListener {
                            networkState.value = NetworkState.LOADED
                            responseSlide.value = product
                        }
                        ?.addOnFailureListener { err ->
                            networkState.postValue(NetworkState.error(err.message))
                        }
                } else networkState.value = NetworkState.error(task.exception?.message)
            }?.addOnFailureListener { err ->
                networkState.value = NetworkState.error(err.message)
            }

        }
        return ResultData(
            data = responseSlide,
            networkState = networkState
        )
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

    override suspend fun sendNotification(product: ProductAdd) {
        withContext(Dispatchers.IO) {
             apiService.sendNotificationAsync(product)
        }
    }

}