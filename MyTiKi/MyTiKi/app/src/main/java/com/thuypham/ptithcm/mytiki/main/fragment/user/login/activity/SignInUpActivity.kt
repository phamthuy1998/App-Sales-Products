package com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.SharedPreference
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.help.isEmailValid
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment.ForgotPasswordFragment
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment.SignInUpFragment
import com.thuypham.ptithcm.mytiki.main.fragment.user.model.User
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.loading_layout.*
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.view.UserFragment


class SignInUpActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
                .of(this)
                .get(UserViewModel::class.java)
    }
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_up)
        ViewModelProviders
                .of(this)
                .get(UserViewModel::class.java)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        showFragment(SignInUpFragment())
    }

    // click button X -> cancel login
    fun onBtnCancelSignInUpFragment(view: View) {
        finish()
    }


    // Click forgot password
    fun onClickForgotPassword(view: View) {
        Toast.makeText(
                applicationContext, "Click forgot",
                Toast.LENGTH_SHORT
        ).show()
        // Show fragment forgot password
        showFragment(ForgotPasswordFragment())
    }

    //  Click login in sign in fragment
    fun onClickLogin(view: View) {
        val email = edt_email_sign_in.text.trim().toString()
        val password = edt_password_sign_in.text.trim().toString()
        if (email.isEmpty()) {
            edt_email_sign_in.error = getString(R.string.error_input_email_not_entered)

        } else if (isEmailValid(email) == false)
            edt_email_sign_in.error = getString(R.string.error_input_email_not_correct)
        else
            edt_email_sign_in.error = null
        if (password.equals("")) {
            edt_password_sign_in.error = getString(R.string.error_input_pw_not_entered)
        } else
            edt_password_sign_in.error = null

        if (!email.isEmpty() && !password.isEmpty()) {
            // Show progress when click login
            progress.visibility = View.VISIBLE
            mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        progress.visibility = View.GONE
                        if (task.isSuccessful) {
                            println("dang nhap thanh cong")
                            Toast.makeText(
                                    this, "Authentication success.",
                                    Toast.LENGTH_LONG
                            ).show()
                            loginSuccess(email, password)
                            finish()

                        }
                        // If can't sign in, toast error
                        else {
                            try {
                                throw task.getException()!!
                            } catch (emailNotExist: FirebaseAuthInvalidUserException) {
                                edt_email_sign_in.error = getString(R.string.error_input_email_not_exists)
                                Toast.makeText(
                                        this, getString(R.string.error_input_email_not_exists),
                                        Toast.LENGTH_LONG
                                ).show()
                            } catch (password: FirebaseAuthInvalidCredentialsException) {
                                edt_password_sign_in.error = getString(R.string.error_input_pw_invalid)
                                Toast.makeText(
                                        this, getString(R.string.error_input_pw_invalid),
                                        Toast.LENGTH_LONG
                                ).show()
                            } catch (error: Exception) {

                                // If sign in fails, display a message to the user.
                                println("dang nhap that bai")
                                Toast.makeText(
                                        this, getString(R.string.error_lgin_false),
                                        Toast.LENGTH_LONG
                                ).show()
                            }
                            loginFalse()
                        }

                    }
        }

    }

    // if login success, we will save infor that you had entered
    // the infor will be save in SharedPreference
    private fun loginSuccess(email: String, password: String) {
        val sharedPreference: SharedPreference = SharedPreference(applicationContext)
        sharedPreference.save(PhysicsConstants.IS_LOGIN, true)
        sharedPreference.save(PhysicsConstants.EMAIL_OR_PHONE, email)
        sharedPreference.save(PhysicsConstants.PASSWORD, password)
    }

    // if login fail, we will remove all of infor that you had entered
    // the infor will be remove in SharedPreference
    private fun loginFalse() {
        val sharedPreference = SharedPreference(applicationContext)
        sharedPreference.removeValue(PhysicsConstants.EMAIL_OR_PHONE)
        sharedPreference.removeValue(PhysicsConstants.IS_LOGIN)
        sharedPreference.removeValue(PhysicsConstants.PASSWORD)
        sharedPreference.save(PhysicsConstants.IS_LOGIN, false)
    }

    // if click back to sign, will change view from  forgotpasswowd to user fragment
    // I don't know how to change from forgotfragment to sign in fragment
    // It's not work for me
    fun onClickBackToSignInFragment(view: View) {
        finish()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frgMainUser, fragment)
                .commit()

    }

}
