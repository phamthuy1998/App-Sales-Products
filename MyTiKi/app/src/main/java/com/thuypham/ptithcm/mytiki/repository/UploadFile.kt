package com.thuypham.ptithcm.mytiki.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import java.util.*

interface UploadFile {

    fun upLoadImage(imageUri: Uri, branchName: String): ResultData<String>
}

class UploadFileImpl : UploadFile {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }
    private val fireStore: FirebaseStorage? by lazy {
        Firebase.storage
    }

    private fun databaseRef() = firebaseDatabase?.reference
    private fun storeRef() = fireStore?.reference

    override fun upLoadImage(imageUri: Uri, branchName: String): ResultData<String> {
        val responseListSlide = MutableLiveData<String>()
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        val ref = storeRef()?.child(branchName)?.child(UUID.randomUUID().toString())
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
                    val downloadUri = task.result.toString()
                    networkState.value = NetworkState.LOADED
                    responseListSlide.value = downloadUri
                } else networkState.value = NetworkState.error(task.exception?.message)
            }?.addOnFailureListener { err ->
                networkState.value = NetworkState.error(err.message)
            }
        return ResultData(
            data = responseListSlide,
            networkState = networkState
        )
    }

}