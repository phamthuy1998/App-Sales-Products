package com.thuypham.ptithcm.mytiki.feature.authentication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.SharedPreference
import com.thuypham.ptithcm.mytiki.util.isEmailValid
import com.thuypham.ptithcm.mytiki.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.loading_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SignInUpActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    val userViewModel: UserViewModel by viewModel()
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_up)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(Constant.USER)
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
        val email = edt_email_sign_in.text?.trim().toString()
        val password = edt_password_sign_in.text?.trim().toString()
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
        sharedPreference.save(Constant.IS_LOGIN, true)
        sharedPreference.save(Constant.EMAIL_OR_PHONE, email)
        sharedPreference.save(Constant.PASSWORD, password)
    }

    // if login fail, we will remove all of infor that you had entered
    // the infor will be remove in SharedPreference
    private fun loginFalse() {
        val sharedPreference = SharedPreference(applicationContext)
        sharedPreference.removeValue(Constant.EMAIL_OR_PHONE)
        sharedPreference.removeValue(Constant.IS_LOGIN)
        sharedPreference.removeValue(Constant.PASSWORD)
        sharedPreference.save(Constant.IS_LOGIN, false)
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
