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
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.repository.SlideRepository
import com.thuypham.ptithcm.mytiki.util.Constant
import java.util.*
import kotlin.collections.ArrayList

class SlideRepositoryImpl : SlideRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }
    private val fireStore: FirebaseStorage? by lazy {
        Firebase.storage
    }

    private fun databaseRef() = firebaseDatabase?.reference
    private fun storeRef() = fireStore?.reference

    override fun getAllSide(): ResultData<ArrayList<Slide>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSlide = MutableLiveData<ArrayList<Slide>>()
        networkState.postValue(NetworkState.LOADING)
        val listSlide = ArrayList<Slide>()
        var slide: Slide?
        val query = databaseRef()?.child(Constant.SLIDE)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        slide = ds.getValue(Slide::class.java)
                        slide?.let { listSlide.add(it) }
                    }
                    responseListSlide.value = listSlide
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List slide  are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListSlide,
            networkState = networkState
        )
    }

    override fun getAllSlideOfCategory(categoryID: String): ResultData<ArrayList<Slide>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSlide = MutableLiveData<ArrayList<Slide>>()
        networkState.postValue(NetworkState.LOADING)
        val listSlide = ArrayList<Slide>()
        var slide: Slide?
        val query = databaseRef()?.child(Constant.SLIDE)?.orderByChild(Constant.SLIDE_ID_CATEGORY)
            ?.equalTo(categoryID)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        slide = ds.getValue(Slide::class.java)
                        slide?.let { listSlide.add(it) }
                    }
                    responseListSlide.value = listSlide
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List slide are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListSlide,
            networkState = networkState
        )
    }

    override fun addSlide(slide: Slide, imageUri: Uri?): ResultData<Slide> {
        val responseSlide = MutableLiveData<Slide>()
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        if (imageUri == null) {
            if(slide.id==null){
                val idPush = databaseRef()?.child(Constant.SLIDE)?.push()?.key
                slide.id = idPush
            }
            databaseRef()?.child(Constant.SLIDE)?.child(slide.id.toString())?.setValue(slide)
                ?.addOnCompleteListener {
                    networkState.value = NetworkState.LOADED
                }
                ?.addOnFailureListener { err ->
                    networkState.postValue(NetworkState.error(err.message))
                }
        } else {
            val ref = storeRef()?.child(Constant.SLIDE)?.child(UUID.randomUUID().toString())
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
                    if(slide.id==null){
                        val idPush = databaseRef()?.child(Constant.SLIDE)?.push()?.key
                        slide.id = idPush
                    }
                    slide.image = downloadUri.toString()
                    databaseRef()?.child(Constant.SLIDE)?.child(slide.id.toString())?.setValue(slide)
                        ?.addOnCompleteListener {
                            networkState.value = NetworkState.LOADED
                            responseSlide.value = slide
                        }
                        ?.addOnFailureListener { err ->
                            networkState.postValue(NetworkState.error(err.message))
                        }
                } else networkState.value = NetworkState.error(task.exception?.message)
            }?.addOnFailureListener {err->
                networkState.value = NetworkState.error(err.message)
            }

        }
        return ResultData(
            data = responseSlide,
            networkState = networkState
        )
    }

    override fun updateSlide(slide: Slide): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(Constant.SLIDE)?.child(slide.id.toString())?.setValue(slide)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun delSlide(slideID: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        val query = databaseRef()?.child(Constant.SLIDE)?.child(slideID)
        query?.removeValue()?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

}