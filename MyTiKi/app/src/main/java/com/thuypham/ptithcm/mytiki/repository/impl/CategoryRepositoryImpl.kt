package com.thuypham.ptithcm.mytiki.repository.impl

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.CategoryRepository
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.Constant.CATEGORY
import java.util.*
import kotlin.collections.ArrayList

class CategoryRepositoryImpl : CategoryRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }
    private val fireStore: FirebaseStorage? by lazy {
        Firebase.storage
    }

    private fun storeRef() = fireStore?.reference

    private fun databaseRef() = firebaseDatabase?.reference

    override fun getAllCategory(): ResultData<ArrayList<Category>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListCategory = MutableLiveData<ArrayList<Category>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Category>()
        var category: Category?
        val query = databaseRef()?.child(CATEGORY)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        category = ds.getValue(Category::class.java)
                        if (category?.del == false)
                            category?.let { listProduct.add(it) }
                    }
                    responseListCategory.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List category are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListCategory,
            networkState = networkState
        )
    }

    override fun addCategory(category: Category, imageUri: Uri?): ResultData<Category> {
//        val networkState = MutableLiveData<NetworkState>()
//        networkState.value = NetworkState.LOADING
//
//        val idPush = databaseRef()?.child(CATEGORY)?.push()?.key
//        category.id = idPush
//        databaseRef()?.child(CATEGORY)?.child(idPush.toString())?.setValue(category)
//            ?.addOnCompleteListener {
//                networkState.value = NetworkState.LOADED
//            }
//            ?.addOnFailureListener { err ->
//                networkState.postValue(NetworkState.error(err.message))
//            }
//        return networkState

        val responseSlide = MutableLiveData<Category>()
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        if (imageUri == null) {
            if (category.id == null) {
                val idPush = databaseRef()?.child(CATEGORY)?.push()?.key
                category.id = idPush
            }
            databaseRef()?.child(CATEGORY)?.child(category.id.toString())?.setValue(category)
                ?.addOnCompleteListener {
                    networkState.value = NetworkState.LOADED
                }
                ?.addOnFailureListener { err ->
                    networkState.postValue(NetworkState.error(err.message))
                }
        } else {
            val ref = storeRef()?.child(CATEGORY)?.child(UUID.randomUUID().toString())
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
                    if (category.id == null) {
                        val idPush = databaseRef()?.child(CATEGORY)?.push()?.key
                        category.id = idPush
                    }
                    category.image = downloadUri.toString()
                    databaseRef()?.child(CATEGORY)?.child(category.id.toString())
                        ?.setValue(category)
                        ?.addOnCompleteListener {
                            networkState.value = NetworkState.LOADED
                            responseSlide.value = category
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

    override fun updateCategory(category: Category): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(CATEGORY)?.child(category.id.toString())?.setValue(category)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun delCategory(categoryID: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(CATEGORY)?.child(categoryID)?.child(Constant.CATEGORY_DEL)
            ?.setValue(true)
            ?.addOnSuccessListener {
                networkState.postValue(NetworkState.LOADED)
            }?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message.toString()))
            }
        return networkState
    }

}