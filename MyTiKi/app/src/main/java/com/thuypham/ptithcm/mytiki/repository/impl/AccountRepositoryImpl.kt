package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AccountRepository
import com.thuypham.ptithcm.mytiki.util.Constant.USER
import com.thuypham.ptithcm.mytiki.util.Constant.USER_IS_DEL
import com.thuypham.ptithcm.mytiki.util.Constant.USER_ROLE
import com.thuypham.ptithcm.mytiki.util.ERR_EMAIL_EXIST
import com.thuypham.ptithcm.mytiki.util.ERR_EMAIL_INVALID
import com.thuypham.ptithcm.mytiki.util.ERR_WEAK_PASSWORD


class AccountRepositoryImpl : AccountRepository {

    private val firebaseAuth: FirebaseAuth? by lazy {
        getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser

    private fun databaseRef() = firebaseDatabase?.reference

    override fun createAcc(user: User): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.createUserWithEmailAndPassword(
            user.email.toString(),
            user.password.toString()
        )?.addOnCompleteListener {
            if (it.isSuccessful) {
                verifyEmail()
                user.id = currentUser()?.uid
                user.active = false
                user.del = false
                databaseRef()?.child(USER)?.child(currentUser()?.uid.toString())?.setValue(user)
                networkState.postValue(NetworkState.LOADED)
            } else
                try {
                    throw it.exception!!
                } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_WEAK_PASSWORD).message))
                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_INVALID).message))
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_EXIST).message))
                } catch (error: Exception) {
                    networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                }
        }
        return networkState
    }

    override fun updateAccount(user: User): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(USER)?.child(user.id.toString())?.setValue(user)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun getAllEmployee(): ResultData<ArrayList<User>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListAcc = MutableLiveData<ArrayList<User>>()
        networkState.postValue(NetworkState.LOADING)
        val listUser = ArrayList<User>()
        var user: User?
        val query = databaseRef()?.child(USER)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        user = ds.getValue(User::class.java)
                        if (user?.role != 1L) user?.let { listUser.add(it) }
                    }
                    responseListAcc.value = listUser
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List employee are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListAcc,
            networkState = networkState
        )
    }

    override fun getEmployeeByRole(role: Long): ResultData<ArrayList<User>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListAcc = MutableLiveData<ArrayList<User>>()
        networkState.postValue(NetworkState.LOADING)
        val listUser = ArrayList<User>()
        var user: User?
        val query = databaseRef()?.child(USER)?.orderByChild(USER_ROLE)?.equalTo(role.toDouble())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        user = ds.getValue(User::class.java)
                        user?.let { listUser.add(it) }
                    }
                    responseListAcc.value = listUser
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List employee are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListAcc,
            networkState = networkState
        )
    }

    override fun deleteAcc(user: User): MutableLiveData<NetworkState> {
        /* val networkState = MutableLiveData<NetworkState>()

         // Get auth credentials from the user for re-authentication. The example below shows
         // email and password credentials but there are multiple possible providers,
         // such as GoogleAuthProvider or FacebookAuthProvider.
         val credential = EmailAuthProvider
             .getCredential(user.email.toString(), user.password.toString())

         // Prompt the user to re-provide their sign-in credentials
         currentUser()?.reauthenticate(credential)?.addOnCompleteListener {
             currentUser()?.delete()?.addOnCompleteListener {
                 databaseRef()?.child(USER)?.child(user.id.toString())?.removeValue()
                 networkState.postValue(NetworkState.LOADED)
             }?.addOnFailureListener{err->
                 networkState.postValue(NetworkState.error(err.message.toString()))
             }
         }*/

        val networkState = MutableLiveData<NetworkState>()
        networkState.postValue(NetworkState.LOADING)
        databaseRef()?.child(USER)?.child(user.id.toString())?.child(USER_IS_DEL)?.setValue(true)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }

        return networkState
    }

    private fun verifyEmail() {
        currentUser()?.sendEmailVerification()
    }

}