package com.thuypham.ptithcm.mytiki.feature.customer.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.util.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.layout_input_birthday.*
import kotlinx.android.synthetic.main.loading_layout.*


class EditProfileActivity : AppCompatActivity() {
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    private var user: User? = null
    private var changePassword: Boolean = false

    // to save info that user had entered to edit profile
    private var userInput: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(Constant.USER)
        mAuth = FirebaseAuth.getInstance()
        setTextDefault()
        addEvent()
    }

    // get Infor user had entered
    private fun getInfoInput(): Int {
        val name = edt_name_edit.text?.trim().toString()
        val phone = edt_phone_edit.text?.trim().toString()
        val birthday = edt_birthday_sign_up.text?.trim().toString()
        val oldPassword = edt_old_pasword_edit.text?.trim().toString()
        val newPassword = edt_new_pasword_edit.text?.trim().toString()
        val reNewPassword = edt_retype_new_pasword_edit.text?.trim().toString()
        changePassword = ck_change_pasword.isChecked
        val genderCheck = rad_male_edit.isChecked

        // check user input correct?
        if (name.isEmpty()) {
            edt_name_edit.error =
                getString(R.string.error_input_name_not_entered)
            Toast.makeText(
                applicationContext,
                getString(R.string.error_input_name_not_entered),
                Toast.LENGTH_LONG
            ).show()
        } else if (phone.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_input_name_not_entered),
                Toast.LENGTH_LONG
            ).show()
            edt_phone_edit.error =
                getString(R.string.error_input_name_not_entered)
        } else if (!isPhoneValid(phone)) {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_input_phone_not_correct),
                Toast.LENGTH_LONG
            ).show()
            edt_phone_edit.error =
                getString(R.string.error_input_phone_not_correct)
        }
        // If check change password
        else if (changePassword) {
            // if old password is empty
            if (oldPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_old_passwords_is_empty),
                    Toast.LENGTH_LONG
                ).show()
                edt_old_pasword_edit.error =
                    getString(R.string.error_old_passwords_is_empty)
            }
            // If new password is empty
            else if (newPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_new_pw_empty),
                    Toast.LENGTH_LONG
                ).show()
                edt_new_pasword_edit.error =
                    getString(R.string.error_new_pw_empty)
            }
            //if retype new password is empty
            else if (reNewPassword.isEmpty()) {
                Toast.makeText(
                    applicationContext, getString(R.string.error_re_new_pw_empty), Toast.LENGTH_LONG
                ).show()
                edt_retype_new_pasword_edit.error =
                    getString(R.string.error_re_new_pw_empty)
            }
            //if old password is incorrect
            else if (oldPassword != user?.password) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_old_passwords_incorrect),
                    Toast.LENGTH_LONG
                ).show()
                edt_old_pasword_edit.error =
                    getString(R.string.error_old_passwords_incorrect)
            }
            // if new password is not valid, like not length, too weak...
            else if (!isPasswordValid(newPassword)) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_new_pw_not_length),
                    Toast.LENGTH_LONG
                ).show()
                edt_new_pasword_edit.error =
                    getString(R.string.error_new_pw_not_length)
            }
            // if new password and retype new password is not match
            else if (newPassword != reNewPassword) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_passwords_do_not_match),
                    Toast.LENGTH_LONG
                ).show()
                edt_retype_new_pasword_edit.error =
                    getString(R.string.error_passwords_do_not_match)
            } else {
                userInput = User(
                    mAuth?.currentUser?.uid,
                    name,
                    phone,
                    user?.email,
                    newPassword,
                    birthday,
                    genderCheck,
                    user?.daycreate,
                    1,
                    active = true,
                    del = false
                )
                // user has changed password
                return 1
            }
        }
        // if didn't change password
        else {
            userInput = User(
                mAuth?.currentUser?.uid,
                name,
                phone,
                user?.email,
                user?.password,
                birthday,
                genderCheck,
                user?.daycreate
                , active = true,
                del = false
            )
            //user not change password
            return 2
        }

        //has some error of user input
        return -1
    }

    // Compare info of user and user just input, if it's same same then not save
    // and it's have different info, check it
    private fun isEditProfile(): Boolean {
        if (!(user?.name.equals(userInput?.name) && user?.phone.equals(userInput?.phone) && user?.birthday.equals(
                userInput?.birthday
            ) && !changePassword && user?.gender == userInput?.gender!!)
        ) {
            return true
        }
        return false
    }

    private fun setTextDefault() {
        var name: String
        var gender: Boolean
        var dayCreate: String
        var birthday: String
        var phone: String
        var password: String

        // get info of user from firebase
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        val email = mUser.email
        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child(Constant.NAME).value as String
                gender = snapshot.child(Constant.GENDER).value as Boolean
                dayCreate = snapshot.child(Constant.DAY_CREATE).value as String
                birthday = snapshot.child(Constant.BIRTHDAY).value as String
                phone = snapshot.child(Constant.PHONE).value as String
                password = snapshot.child(Constant.PASSWORD).value as String
                edt_name_edit.setText(name)
                edt_phone_edit.setText(phone)
                edt_birthday_sign_up.setText(birthday)

                if (gender) rad_male_edit.isChecked = true
                else rad_female_edit.isChecked = true
                // save info into user
                user = User(
                    mUser.uid,
                    name,
                    phone,
                    email,
                    password,
                    birthday,
                    gender,
                    dayCreate
                    , active = true,
                    del = false
                )

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
        //check uer had edited profile yet?
        // if not, show toast that user had not changed data

        //If user checked to change password
        val check  = getInfoInput()
        if (check == 1) {
            // if user's profile didn't change
            if (!isEditProfile()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.user_not_change),
                    Toast.LENGTH_LONG
                ).show()
            }

            // if yes, check info that user had entered
            // including change infor user and update password
            else {
                // show progress
                progress.visibility = View.VISIBLE
                after(2000, process = {

                })

                updatePassword(userInput?.password!!)
                updateUser(userInput)
            }
        }

        // if user didn't check(checkbox) to change password
        if (check == 2) {
            if (!isEditProfile()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.user_not_change),
                    Toast.LENGTH_LONG
                ).show()
            }
            // if yes, check infor that user had entered
            // save info change of user
            else updateUser(userInput)
        }

    }

    private fun updatePassword(password: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.updatePassword(password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.user_change_success),
                    Toast.LENGTH_LONG
                ).show()
                val sharedPreference = SharedPreference(applicationContext)
                sharedPreference.removeValue(Constant.PASSWORD)
                sharedPreference.save(Constant.PASSWORD, password)
            }
            progress.visibility = View.GONE
        }
    }


    private fun updateUser(user: User?) {
        progress.visibility = View.VISIBLE
        val userID = mAuth?.currentUser?.uid
        mDatabaseReference =
            FirebaseDatabase.getInstance().getReference(Constant.USER).child(userID.toString())
        mDatabaseReference?.child(Constant.NAME)?.setValue(user?.name)
        mDatabaseReference?.child(Constant.GENDER)?.setValue(user?.gender)
        mDatabaseReference?.child(Constant.BIRTHDAY)?.setValue(user?.birthday)
        mDatabaseReference?.child(Constant.PASSWORD)?.setValue(user?.password)
        mDatabaseReference?.child(Constant.PHONE)?.setValue(user?.phone)
        updatePasswordSuccess()

//        val userMap = HashMap<String, String>()
//        userMap[Constant.NAME] = user?.name!!
//        userMap[Constant.GENDER] = user.gender!!
//        userMap[Constant.BIRTHDAY] = user.birthday!!
//        userMap[Constant.PASSWORD] = user.password!!
//        userMap[Constant.PHONE] = user.phone!!
//        userMap[Constant.EMAIL] = user.email!!
//        userMap[Constant.DAY_CREATE] = user.daycreate!!
//
//        mDatabaseReference?.setValue(userMap)?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                updatePasswordSuccess()
//            } else {
//                updatePasswordFail()
//            }
//
//        }

        progress.visibility = View.GONE
        // hide layout edit password
        ll_edit_profile.visibility = View.GONE
        edt_old_pasword_edit.setText("")
        edt_new_pasword_edit.setText("")
        edt_retype_new_pasword_edit.setText("")
        ck_change_pasword.isChecked = false
    }


    //user info had been updated success
    private fun updatePasswordSuccess() {
        println("success Update user")
        Toast.makeText(
            applicationContext, getString(R.string.user_change_success),
            Toast.LENGTH_LONG
        ).show()
    }

    //user info had been updated fail
    private fun updatePasswordFail() {
        println("Error Update user")
        Toast.makeText(
            applicationContext,
            getString(R.string.user_update_user_error),
            Toast.LENGTH_LONG
        ).show()
    }

    // Show calendar to select birthday
    private fun showCalendar() {
        val newFragment = DatePickerFragment()
        // Show the date picker dialog
        newFragment.show(supportFragmentManager, "Choose a date of birth")
    }


    fun onClickBackToUserFragment(view: View) {
        finish()
    }

}