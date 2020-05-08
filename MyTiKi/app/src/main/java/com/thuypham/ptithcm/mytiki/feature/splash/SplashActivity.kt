package com.thuypham.ptithcm.mytiki.feature.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.SharedPreference
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity(), Animation.AnimationListener {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        enablePersistence()
        ivLogo?.apply {
            doOnLayout {
                val animation =
                    AnimationUtils.loadAnimation(this@SplashActivity, R.anim.anim_splash)
                startAnimation(animation)
                animation?.setAnimationListener(this@SplashActivity)
            }
        }
    }

    private fun enablePersistence() {
        // [START rtdb_enable_persistence]
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        // [END rtdb_enable_persistence]
    }

    private fun loginUser() {
        val sharedPreference: SharedPreference =
            SharedPreference(applicationContext)
        mAuth = FirebaseAuth.getInstance()
        val isLogin = sharedPreference.getValueBoolien(Constant.IS_LOGIN, false)
        if (isLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val email = sharedPreference.getValueString(Constant.EMAIL_OR_PHONE).toString()
            val password = sharedPreference.getValueString(Constant.PASSWORD).toString()

            if (!email.equals("") && !password.equals("")) {
                mAuth?.signInWithEmailAndPassword(
                    email, password
                )
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

    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        openNextScreen()
    }

    private fun openNextScreen() {
//        if (FirebaseAuth.getInstance().currentUser == null)
//            startActivity<AuthActivity>()
//        else{
//
//        }


        startActivity<MainActivity>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onAnimationStart(animation: Animation?) {}
}
