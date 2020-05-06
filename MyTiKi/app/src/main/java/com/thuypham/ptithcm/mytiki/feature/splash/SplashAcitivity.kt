package com.thuypham.ptithcm.mytiki.feature.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.util.PhysicsConstants
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import com.thuypham.ptithcm.mytiki.util.SharedPreference

class SplashAcitivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null


    val userViewModel by lazy {
        ViewModelProviders.of(this)
                .get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        ViewModelProviders
                .of(this)
                .get(UserViewModel::class.java)

        loginUser()
    }

    fun loginUser() {
        val sharedPreference: SharedPreference =
            SharedPreference(applicationContext)
        mAuth = FirebaseAuth.getInstance()
        val isLogin = sharedPreference.getValueBoolien(PhysicsConstants.IS_LOGIN, false)
        if (isLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val email = sharedPreference.getValueString(PhysicsConstants.EMAIL_OR_PHONE).toString()
            val password = sharedPreference.getValueString(PhysicsConstants.PASSWORD).toString()

            if (!email.equals("") && !password.equals("")) {
                mAuth?.signInWithEmailAndPassword(
                        email, password)
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                            } else {
                                // If sign in fails, display a message to the user.
                            }
                        }
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}
