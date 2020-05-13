package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.repository.AuthRepository
import com.thuypham.ptithcm.mytiki.util.*
import com.thuypham.ptithcm.mytiki.util.Constant.EMAIL
import com.thuypham.ptithcm.mytiki.util.Constant.USER
import com.thuypham.ptithcm.mytiki.util.Constant.USER_IS_ACTIVE


class AuthRepositoryImpl : AuthRepository {

    private val firebaseAuth: FirebaseAuth? by lazy {
        getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser

    private fun databaseRef() = firebaseDatabase?.reference

    override fun login(email: String, password: String): ResultData<User> {
        val networkState = MutableLiveData<NetworkState>()
        val responseLogin = MutableLiveData<User>()
        networkState.postValue(NetworkState.LOADING)

        firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Check email Verified
                    if (currentUser()?.isEmailVerified == true) {
                        // Set email active
                        databaseRef()?.child(USER)?.child(currentUser()?.uid.toString())
                            ?.child(USER_IS_ACTIVE)?.setValue(true)

                        val query = databaseRef()?.child(USER)
                            ?.child(currentUser()?.uid.toString())

                        val valueEventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    if (user?.del == false) {
                                        responseLogin.value = user
                                        networkState.postValue(NetworkState.LOADED)
                                    } else networkState.postValue(NetworkState.error("Your account has been locked!"))
                                } else networkState.postValue(NetworkState.error("Can't load info of your account!"))
                            }

                            override fun onCancelled(databaseError: DatabaseError) =
                                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
                        }
                        query?.addValueEventListener(valueEventListener)
                    } else
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_VERIFY).message))
                } else
                    try {
                        throw it.exception!!
                    } catch (emailNotExist: FirebaseAuthInvalidUserException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_NOT_Exist).message))
                    } catch (password: FirebaseAuthInvalidCredentialsException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_INCORRECT_PW).message))
                    } catch (error: Exception) {
                        networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                    }
            }
        return ResultData(
            data = responseLogin,
            networkState = networkState
        )
    }

    override fun logOut(): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseUpdateInfo = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.signOut()
        networkState.postValue(NetworkState.LOADED)
        return ResultData(
            data = responseUpdateInfo,
            networkState = networkState
        )
    }

    override fun updateUserInfo(user: User): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseUpdateInfo = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)

        databaseRef()?.child(USER)?.child(currentUser()?.uid.toString())?.setValue(user)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
                responseUpdateInfo.value = true
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
                responseUpdateInfo.value = false
            }

        return ResultData(
            data = responseUpdateInfo,
            networkState = networkState
        )
    }

    override fun sendMailResetPassword(email: String): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSendMail = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    responseSendMail.value = true
                    networkState.postValue(NetworkState.LOADED)
                } else
                    try {
                        throw it.exception!!
                    } catch (emailNotExist: FirebaseAuthInvalidUserException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_NOT_Exist).message))
                    } catch (error: Exception) {
                        networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                    }
            }
        return ResultData(
            data = responseSendMail,
            networkState = networkState
        )
    }

    override fun register(user: User): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseRegister = MutableLiveData<Boolean>()
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
                responseRegister.value = true
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
        return ResultData(
            data = responseRegister,
            networkState = networkState
        )
    }

    override fun getAccInfo(email: String): ResultData<User> {
        val networkState = MutableLiveData<NetworkState>()
        val responseUser = MutableLiveData<User>()
        networkState.postValue(NetworkState.LOADING)
        val query = databaseRef()?.child(USER)
            ?.orderByChild(EMAIL)
            ?.equalTo(email)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(User::class.java)
                        responseUser.value = user
                        networkState.postValue(NetworkState.LOADED)
                    }
                } else {
                    networkState.postValue(NetworkState.error("Can't load info this acc!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseUser,
            networkState = networkState
        )
    }

    override fun getCurrentUser(): ResultData<User> {
        val networkState = MutableLiveData<NetworkState>()
        val responseUser = MutableLiveData<User>()
        networkState.postValue(NetworkState.LOADING)
        val query = databaseRef()?.child(USER)
            ?.orderByChild(EMAIL)
            ?.equalTo(currentUser()?.email)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val user = ds.getValue(User::class.java)
                        responseUser.value = user
                        networkState.postValue(NetworkState.LOADED)
                    }
                } else {
                    networkState.postValue(NetworkState.error("Can't load info this acc!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseUser,
            networkState = networkState
        )
    }

    private fun verifyEmail() {
        currentUser()?.sendEmailVerification()
    }

}