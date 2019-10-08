package com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.databinding.FragmentForgotPasswordBinding
import com.thuypham.ptithcm.mytiki.help.after
import com.thuypham.ptithcm.mytiki.help.isEmailValid
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.loading_layout.*


class ForgotPasswordFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
                .of(activity!!)
                .get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mAuth = FirebaseAuth.getInstance()
        val binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@ForgotPasswordFragment
        binding.userViewModel = userViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addEvent()
    }

    private fun addEvent() {
        btn_reset_password.setOnClickListener {
            progress.visibility = View.VISIBLE
            after(2000, process = {
                sendPasswordResetEmail()
            })
        }

    }

    private fun sendPasswordResetEmail() {
        val email = edt_email_forgot_password?.text?.trim().toString()
        if (email.isEmpty()) {
            edt_email_forgot_password.setText(getString(R.string.error_input_email_not_entered))
        } else if (isEmailValid(email) == false) {
            edt_email_forgot_password.setText(getString(R.string.error_input_email_not_correct))
        }
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                    .sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val message = "Email sent."
                            Log.d(tag, message)
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                            updateUI()
                        } else {
                            Log.w(tag, task.exception?.message)
                            Toast.makeText(requireContext(), getString(R.string.error_input_email_not_exists), Toast.LENGTH_LONG).show()
                        }
                    }
        } else {
            Toast.makeText(requireContext(), "Enter Email", Toast.LENGTH_SHORT).show()
        }
        progress.visibility= View.GONE
    }

    private fun updateUI() {
        activity?.finish()
    }

    fun checkEmailExists(email: String): Boolean {
        var checkEmailExist = true;
        progress.visibility = View.VISIBLE
        mAuth?.fetchSignInMethodsForEmail(email)?.addOnCompleteListener { fetchTask ->
            if (fetchTask.isSuccessful) {
                checkEmailExist = true;
            } else {
                checkEmailExist = false
            }
            progress.visibility = View.GONE
        }
        return checkEmailExist
    }

}