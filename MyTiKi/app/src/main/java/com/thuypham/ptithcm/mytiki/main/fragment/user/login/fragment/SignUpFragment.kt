package com.thuypham.ptithcm.mytiki.main.fragment.user.login.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.*
import com.thuypham.ptithcm.mytiki.help.SharedPreference

import com.thuypham.ptithcm.mytiki.databinding.FragmentSignUpBinding
import com.thuypham.ptithcm.mytiki.help.*
import com.thuypham.ptithcm.mytiki.main.fragment.user.model.User
import com.thuypham.ptithcm.mytiki.main.fragment.user.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.layout_input_birthday.*
import kotlinx.android.synthetic.main.loading_layout.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_verified_email.view.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.Dialog
import android.content.DialogInterface
import kotlinx.android.synthetic.main.dialog_verified_email.*
import kotlinx.android.synthetic.main.dialog_verified_email.view.btn_ok_verifi
import kotlinx.android.synthetic.main.dialog_verified_email.view.tv_email_verified
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class SignUpFragment : Fragment() {
    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    val userViewModel: UserViewModel by lazy {
        ViewModelProviders
                .of(activity!!)
                .get(UserViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@SignUpFragment
        binding.userViewModel = userViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")

        addEvents()
    }

    private fun addEvents() {
        edt_birthday_sign_up.setOnClickListener {
            showCalendar()
            after(2000, process = {

            })
        }
        btn_sign_up.setOnClickListener {
            checkSignUp()
            after(2000, process = {

            })
        }
    }

    @SuppressLint("NewApi")
    private fun checkSignUp() {

        // get text of user had entered
        val name = edt_name_sign_up.text?.trim().toString()
        val phone = edt_phone_sign_up.text?.trim().toString()
        val email = edt_email_sign_up.text?.trim().toString()
        val password = edt_pasword_sign_up.text?.trim().toString()
        val radBtnGroud = rad_btn_gender
        var gender: String?
        val birthDay = edt_birthday_sign_up.text.trim().toString()
        radBtnGroud.let {
            if (rad_male.isChecked)
                gender = getString(com.thuypham.ptithcm.mytiki.R.string.rab_male)
            else if (rad_female.isChecked)
                gender = getString(com.thuypham.ptithcm.mytiki.R.string.female)
            else
                gender = getString(com.thuypham.ptithcm.mytiki.R.string.rab_male)
        }

        // get time current to write into time create acc
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val dayCreate = current.format(formatter)

        // Check value of user had entered correct?
        // Empty name
        if (name.isEmpty()) {
            edt_name_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered)
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                    Toast.LENGTH_LONG).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_entered),
                    Toast.LENGTH_LONG).show()
            edt_phone_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_entered)
        } else if (isPhoneValid(phone) == false) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct),
                    Toast.LENGTH_LONG).show()
            edt_phone_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct)
        } else if (email.isEmpty()) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_entered),
                    Toast.LENGTH_LONG).show()
            edt_email_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_entered)
        } else if (isEmailValid(email) == false) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_correct),
                    Toast.LENGTH_LONG).show()
            edt_email_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_correct)
        } else if (password.isEmpty()) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_entered),
                    Toast.LENGTH_LONG).show()
            edt_pasword_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_entered)
        } else if (isPasswordValid(password) == false) {
            println("loi password")
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_length),
                    Toast.LENGTH_LONG).show()
            edt_pasword_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_length)
        } else if (birthDay.isEmpty()) {
            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                    Toast.LENGTH_LONG).show()
            edt_birthday_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered)
        } else {

            val user = User(name, phone, email, password, birthDay, gender, dayCreate)
            createNewAcc(user, email, password)
        }

    }

    private fun createNewAcc(user: User, email: String, password: String) {
        progress.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(tag, "DDang ky thanh cong")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email
//                        writeNewMessage(email)

//                        verifyEmail();
                        // Send mail success

                        val mUser = mAuth!!.currentUser;
                        mUser!!.sendEmailVerification()
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        println("da gui mail")
                                        Toast.makeText(requireContext(),
                                                "Verification email sent to " + mUser.getEmail(),
                                                Toast.LENGTH_LONG).show()
                                        //update user profile information
                                        val currentUserDb = mDatabaseReference!!.child(userId)
                                        currentUserDb.child("name").setValue(user.name)
                                        currentUserDb.child("phone").setValue(user.phone)
                                        currentUserDb.child("email").setValue(user.email)
                                        currentUserDb.child("password").setValue(user.password)
                                        currentUserDb.child("birthday").setValue(user.dayofbirth)
                                        currentUserDb.child("gender").setValue(user.gender)
                                        currentUserDb.child("daycreate").setValue(user.dayCreateAcc)
                                        // close this fragment, and change to user fragment
                                        Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.create_acc_success),
                                                Toast.LENGTH_LONG).show()
                                        loginSuccess(email, password)

                                        // turn over the screen user fragment
                                        activity?.finish()

                                        //show dialog verifi email
//                                        showDialog(email)
                                    } else {
                                        // if can't send email, delete acc
                                        deleteAccount()
                                        println("k gui dc mail")
                                        Toast.makeText(requireContext(),
                                                "Failed to send verification email. Looks like your email is not correct",
                                                Toast.LENGTH_LONG).show()
                                        loginFalse()
                                    }
                                }

//                        if (isSendMail == true) {
//                            mAuth = FirebaseAuth.getInstance()
//                            val mUser = mAuth!!.currentUser
//                            val checkVerified = mUser?.isEmailVerified
//                            // if email had verified
//                            if (checkVerified == true) {
//                                //update user profile information
//                                val currentUserDb = mDatabaseReference!!.child(userId)
//                                currentUserDb.child("name").setValue(user.name)
//                                currentUserDb.child("phone").setValue(user.phone)
//                                currentUserDb.child("email").setValue(user.email)
//                                currentUserDb.child("password").setValue(user.password)
//                                currentUserDb.child("birthday").setValue(user.dayofbirth)
//                                currentUserDb.child("gender").setValue(user.gender)
//                                currentUserDb.child("daycreate").setValue(user.dayCreateAcc)
//                                // close this fragment, and change to user fragment
//                                Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.create_acc_success),
//                                        Toast.LENGTH_LONG).show()

//                            } else {
//                                showDialog(email)
//                            }

                    } else {
                        println("dang ky that bai moi vo day ma")
                        try {
                            throw task.getException()!!
                        } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                            edt_pasword_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_length)
                            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_pw_not_length),
                                    Toast.LENGTH_LONG).show()
                        } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                            edt_email_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_correct)
                            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_not_correct),
                                    Toast.LENGTH_LONG).show()
                        } catch (existEmail: FirebaseAuthUserCollisionException) {
                            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_exists),
                                    Toast.LENGTH_LONG).show()
                            edt_email_sign_up.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_email_exists)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), getString(com.thuypham.ptithcm.mytiki.R.string.error_signup),
                                    Toast.LENGTH_LONG).show()
                            Log.d(tag, "loi gi do khong biet: " + e.message)
                        }
                    }
                    progress.visibility = View.GONE
                }
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        // delete user currenr
        user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "User account deleted.")
                    } else {
                        Log.d(tag, "User account not deleted.")
                    }
                }

    }

    //  show dialog to warning that user not Verified email when they create acc
    private fun showDialog(email: String) {
//                Theme_Black_NoTitleBar_Fullscreen
        val dialog = Dialog(requireContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setCancelable(false)
        dialog.setContentView(com.thuypham.ptithcm.mytiki.R.layout.dialog_verified_email)

        dialog.tv_email_verified.setText(email)
        dialog.btn_ok_verifi.setOnClickListener { dialog.dismiss() }
        dialog.btn_cancel_active_acc.setOnClickListener { dialog.dismiss() }


        dialog.show()

//        val mDialogView = LayoutInflater.from(requireContext()).inflate(com.thuypham.ptithcm.mytiki.R.layout.dialog_verified_email, null)
//        //AlertDialogBuilder
//        val mBuilder = AlertDialog.Builder(requireContext())
//
//                .setView(mDialogView)
//                .setTitle("Attention")
//        //show dialog
//        val mAlertDialog = mBuilder.show()


    }

    private fun loginSuccess(email: String, password: String) {
        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        sharedPreference.save(PhysicsConstants.IS_LOGIN, true)
        sharedPreference.save(PhysicsConstants.EMAIL_OR_PHONE, email)
        sharedPreference.save(PhysicsConstants.PASSWORD, password)
    }

    private fun loginFalse() {
        val sharedPreference: SharedPreference = SharedPreference(requireContext())
        sharedPreference.removeValue(PhysicsConstants.EMAIL_OR_PHONE)
        sharedPreference.removeValue(PhysicsConstants.IS_LOGIN)
        sharedPreference.removeValue(PhysicsConstants.PASSWORD)
        sharedPreference.save(PhysicsConstants.IS_LOGIN, false)
    }

//    private fun verifyEmail() {
//        val mUser = mAuth!!.currentUser;
//        mUser!!.sendEmailVerification()
//                .addOnCompleteListener(requireActivity()) { task ->
//                    if (task.isSuccessful) {
//                        println("da gui mail")
//                        Toast.makeText(requireContext(),
//                                "Verification email sent to " + mUser.getEmail(),
//                                Toast.LENGTH_LONG).show()
//                    } else {
//                        println("k gui dc mail")
//                        Toast.makeText(requireContext(),
//                                "Failed to send verification email. Looks like your email is not correct",
//                                Toast.LENGTH_LONG).show()
//                    }
//                }
//    }

    // Show calendar to select birthday
    fun showCalendar() {
        val newFragment = DatePickerFragment()
        // Show the date picker dialog
        newFragment.show(fragmentManager, "Choose a date of birth")
    }

}
