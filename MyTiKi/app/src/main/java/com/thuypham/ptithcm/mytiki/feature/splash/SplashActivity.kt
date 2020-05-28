package com.thuypham.ptithcm.mytiki.feature.splash

import android.animation.Animator
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.employee.main.HomeEmployeeActivity
import com.thuypham.ptithcm.mytiki.util.Constant.CUSTOMER
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() , Animator.AnimatorListener {

    private val authViewModel: UserViewModel by viewModel()
    private var isLogin = false
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        animationView.addAnimatorListener(this)

        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance()
                .currentUser?.email?.let { authViewModel.getUserInfoByEmail(it) }
            bindViewModel()
            isLogin = true
        }
    }

    private fun openNextScreen(user: User? = null) {
        /* Not login yet */
        if (!isLogin || user == null)
            startActivity<AuthActivity>()
        else {
            /* Customer */
            if (user.role == CUSTOMER) startActivity<MainActivity>()
            /* Employee, include admin */
            else startActivity<HomeEmployeeActivity>()
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun bindViewModel() {
        authViewModel.userInfo.observe(this, Observer { user ->
            this.user = user
            openNextScreen(user)
        })
        authViewModel.networkStateUserInfo.observe(this){
            if(it.status==Status.FAILED) openNextScreen()
        }

    }

   /* *//* Enable firebase offline mode *//*
    private fun enablePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }*/

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        if (!isLogin) openNextScreen(null)
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}
