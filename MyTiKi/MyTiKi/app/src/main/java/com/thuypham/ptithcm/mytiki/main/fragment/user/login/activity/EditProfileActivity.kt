package com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.*
import com.thuypham.ptithcm.mytiki.main.fragment.user.model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.layout_input_birthday.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.loading_layout.*


class EditProfileActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var user: User? = null
    private var changePassword: Boolean = false

    // to save infor that user had entered to edit profile
    private var userInput: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.thuypham.ptithcm.mytiki.R.layout.activity_edit_profile)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()
        setTextDefault()
        addEvent()
    }

    // get Infor user had entered
    fun getInforInput(): Int {
        var name = edt_name_edit.text?.trim().toString()
        var phone = edt_phone_edit.text?.trim().toString()
        var birthday = edt_birthday_sign_up.text?.trim().toString()
        var oldPassword = edt_old_pasword_edit.text?.trim().toString()
        var newPassword = edt_new_pasword_edit.text?.trim().toString()
        var reNewPassword = edt_retype_new_pasword_edit.text?.trim().toString()
        changePassword = ck_change_pasword.isChecked
        var gender = getString(R.string.rab_male)
        if (rad_male_edit.isChecked == true) {
            println("chon nam")
            gender = getString(R.string.rab_male)
        } else if (rad_female_edit.isChecked == true) {
            gender = getString(R.string.female)
            println("chon nu")
        }

        // check user input correct?
        if (name.isEmpty()) {
            edt_name_edit.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered)
            Toast.makeText(
                applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                Toast.LENGTH_LONG
            ).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(
                applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                Toast.LENGTH_LONG
            ).show()
            edt_phone_edit.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered)
        } else if (isPhoneValid(phone) == false) {
            Toast.makeText(
                applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct),
                Toast.LENGTH_LONG
            ).show()
            edt_phone_edit.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct)
        }
        // If check change password
        else if (changePassword == true) {
            // if old password is empty
            if (oldPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_old_passwords_is_empty),
                    Toast.LENGTH_LONG
                ).show()
                edt_old_pasword_edit.error =
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_old_passwords_is_empty)
            }
            // If new password is empty
            else if (newPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_new_pw_empty),
                    Toast.LENGTH_LONG
                ).show()
                edt_new_pasword_edit.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_new_pw_empty)
            }
            //if retype new password is empty
            else if (reNewPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_re_new_pw_empty),
                    Toast.LENGTH_LONG
                ).show()
                edt_retype_new_pasword_edit.error =
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_re_new_pw_empty)
            }
            //if old password is incorrect
            else if (!oldPassword.equals(user?.password)) {
                println("Mat khau cu edit: ${user?.password}")
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_old_passwords_incorrect),
                    Toast.LENGTH_LONG
                ).show()
                edt_old_pasword_edit.error =
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_old_passwords_incorrect)
            }
            // if new password is not valid, like not length, too weak...
            else if (isPasswordValid(newPassword) == false) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_new_pw_not_length),
                    Toast.LENGTH_LONG
                ).show()
                edt_new_pasword_edit.error = getString(com.thuypham.ptithcm.mytiki.R.string.error_new_pw_not_length)
            }
            // if new password and retype new password is not match
            else if (!newPassword.equals(reNewPassword)) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.error_passwords_do_not_match),
                    Toast.LENGTH_LONG
                ).show()
                edt_retype_new_pasword_edit.error =
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_passwords_do_not_match)
            } else {
                userInput = User(name, phone, user?.email, newPassword, birthday, gender, user?.dayCreateAcc)
                // user has changed password
                return 1;
            }
        }
        // if didn't change password
        else {
            userInput = User(name, phone, user?.email, user?.password, birthday, gender, user?.dayCreateAcc)
            //user not change password
            return 2;
        }

        //has some error of user input
        return -1;
    }

    // Compare infor of user and user just input, if it's same same then not save
    // and it's have different infor, check it
    fun isEditProfile(): Boolean {
        if (!user?.name.equals(userInput?.name) ||
            !user?.phone.equals(userInput?.phone) ||
            !user?.dayofbirth.equals(userInput?.dayofbirth) ||
            changePassword == true ||
            user?.gender?.compareTo(userInput?.gender!!, true) != 0
        ) {
            return true
        }
        return false
    }

    private fun setTextDefault() {
        var name = ""
        var gender = ""
        var dayCreate = ""
        var birthday = ""
        var phone = ""
        var password = ""

        // get infor of user from firebase
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        val email = mUser.email
        println("email edit: $email")
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child(PhysicsConstants.NAME).value as String
                println("name edit: $name")
                gender = snapshot.child(PhysicsConstants.GENDER).value as String
                println("gender edit: $gender")
                dayCreate = snapshot.child(PhysicsConstants.DAY_CREATE).value as String
                birthday = snapshot.child(PhysicsConstants.BIRTHDAY).value as String
                phone = snapshot.child(PhysicsConstants.PHONE).value as String
                password = snapshot.child(PhysicsConstants.PASSWORD).value as String
                edt_name_edit.setText(name)
                edt_phone_edit.setText(phone)
                edt_birthday_sign_up.setText(birthday)

                if (gender.compareTo("Male", true) == 0 || gender.compareTo("Nam", true) == 0) {
                    rad_male_edit.isChecked = true
                    println("nam edit")
                }
                if (gender.compareTo("Female", true) == 0 || gender.compareTo("Nữ", true) == 0) {
                    rad_female_edit.isChecked = true
                    println("Nu edit")
                }
                // save infor into user
                user = User(name, phone, email, password, birthday, gender, dayCreate)

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    private fun addEvent() {
        ck_change_pasword.setOnClickListener {
            if (ck_change_pasword.isChecked) ll_edit_profile.visibility = View.VISIBLE
            else ll_edit_profile.visibility = View.GONE
        }

        edt_birthday_sign_up.setOnClickListener {
            showCalendar()
            after(2000, process = {

            })
        }
    }


    fun onClickEditProfile(view: View) {
        //check uer had editted profile yet?
        // if not, show toast that user had not changed data

        //If user checked to change password
        if (getInforInput() == 1) {
            // if userprofile didn't change
            if (isEditProfile() == false) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_not_change),
                    Toast.LENGTH_LONG
                ).show()
                println("khong co thay doi 1")
            }

            // if yes, check infor that user had entered
            // including change infor user and update password
            else {
                // show progress
                progress.visibility = View.VISIBLE
                after(2000, process = {

                })

                updatePassword(userInput?.password!!)
                updateUser(userInput)
                println("user co thay doi 1")
            }
        }

        // if user didn't check(checkbox) to change password
        if (getInforInput() == 2) {
            if (isEditProfile() == false) {
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_not_change),
                    Toast.LENGTH_LONG
                ).show()
                println("khong co thay doi 2")
            }
            // if yes, check infor that user had entered
            // save infor change of user
            else {
                updateUser(userInput)
                println("user co thay doi 2")
            }
        }

    }

    fun updatePassword(password: String) {
        println("vo thay doi password dang nhap")
        val user = FirebaseAuth.getInstance().currentUser
        user!!.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Update Success")
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_change_success),
                    Toast.LENGTH_LONG
                ).show()
                val sharedPreference: SharedPreference = SharedPreference(applicationContext)
                sharedPreference.removeValue(PhysicsConstants.PASSWORD)
                sharedPreference.save(PhysicsConstants.PASSWORD, password)

            } else {
                println("Error Update")
                Toast.makeText(
                    applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_change_error_pw),
                    Toast.LENGTH_LONG
                ).show()
            }

            // hide progress
            progress.visibility = View.GONE
        }
    }


    fun updateUser(user: User?) {
        progress.visibility = View.VISIBLE
        val userID = mAuth?.currentUser?.uid
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(PhysicsConstants.USERS).child(userID!!)
        var userMap = HashMap<String, String>()
        userMap[PhysicsConstants.NAME] = user?.name!!
        userMap[PhysicsConstants.GENDER] = user?.gender!!
        userMap[PhysicsConstants.BIRTHDAY] = user?.dayofbirth!!
        userMap[PhysicsConstants.PASSWORD] = user?.password!!
        userMap[PhysicsConstants.PHONE] = user?.phone!!
        userMap[PhysicsConstants.EMAIL] = user?.email!!
        userMap[PhysicsConstants.DAY_CREATE] = user?.dayCreateAcc!!
        mDatabaseReference!!.setValue(userMap).addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                updatePasswordSuccess()
            } else {
                updatePasswordFail()
            }
            progress.visibility = View.GONE
            // hide layout editpassword
            ll_edit_profile.visibility = View.GONE
            edt_old_pasword_edit.setText("")
            edt_new_pasword_edit.setText("")
            edt_retype_new_pasword_edit.setText("")
            ck_change_pasword.isChecked= false
        })
    }


    //user infor had been updated succeess
    fun updatePasswordSuccess() {
        println("success Update user")
        Toast.makeText(
            applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_change_success),
            Toast.LENGTH_LONG
        ).show()
    }

    //user infor had been updated fail
    fun updatePasswordFail() {
        println("Error Update user")
        Toast.makeText(
            applicationContext, getString(com.thuypham.ptithcm.mytiki.R.string.user_update_user_error),
            Toast.LENGTH_LONG
        ).show()
    }

    fun updateUserAndPassword() {

    }

    // Show calendar to select birthday
    fun showCalendar() {
        val newFragment = DatePickerFragment()
        // Show the date picker dialog
        newFragment.show(supportFragmentManager, "Choose a date of birth")
    }


    fun onClickBackToUserFragment(view: View) {
        finish()
    }

}