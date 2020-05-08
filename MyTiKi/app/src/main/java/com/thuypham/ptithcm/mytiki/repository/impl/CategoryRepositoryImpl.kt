package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.CategoryRepository
import com.thuypham.ptithcm.mytiki.util.Constant.CATEGORY

class CategoryRepositoryImpl : CategoryRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

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

    override fun addCategory(category: Category): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        val idPush = databaseRef()?.child(CATEGORY)?.push()?.key
        category.id = idPush
        databaseRef()?.child(CATEGORY)?.child(idPush.toString())?.setValue(category)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
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
        val query = databaseRef()?.child(CATEGORY)?.child(categoryID)
        query?.removeValue()?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

}