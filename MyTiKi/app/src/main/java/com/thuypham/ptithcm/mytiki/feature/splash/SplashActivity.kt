package com.thuypham.ptithcm.mytiki.feature.splash

import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.employee.main.MainEmployeeActivity
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity(), Animation.AnimationListener {

    private val authViewModel: UserViewModel by viewModel()
    private var isLogin = false
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        enablePersistence()
        ivLogo?.apply {
            doOnLayout {
                val animation =
                    AnimationUtils.loadAnimation(this@SplashActivity, R.anim.anim_splash)
                startAnimation(animation)
                animation?.setAnimationListener(this@SplashActivity)
            }
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance()
                .currentUser?.email?.let { authViewModel.getUserInfoByEmail(it) }
            bindViewModel()
            isLogin = true
        }
    }

    private fun openNextScreen(isLogin: Boolean, user: User?) {
        /* Not login yet */
        if (!isLogin)
            startActivity<AuthActivity>()
        else {
            /* Customer */
            if (user?.role == 1) startActivity<MainActivity>()
            /* Employee, include admin */
            else startActivity<MainEmployeeActivity>()
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun bindViewModel() {
        authViewModel.userInfo.observe(this, Observer { user ->
            this.user = user
        })
        authViewModel.networkStateUserInfo.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> openNextScreen(true, user)
                Status.FAILED -> openNextScreen(false, null)
                Status.RUNNING -> {
                }
            }
        })
    }

    /* Enable firebase offline mode */
    private fun enablePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }


    override fun onAnimationRepeat(animation: Animation?) {}

    override fun onAnimationEnd(animation: Animation?) {
        if (!isLogin) openNextScreen(false, user)
    }

    override fun onAnimationStart(animation: Animation?) {}
}
