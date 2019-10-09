package com.thuypham.ptithcm.mytiki.main.order.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.help.isPhoneValid
import com.thuypham.ptithcm.mytiki.main.order.adapter.AddressAdapter
import com.thuypham.ptithcm.mytiki.main.order.model.Address
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.dialog_add_new_address.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

class AddressActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //product
    private var addressAdapter: AddressAdapter? = null
    private var addressList = ArrayList<Address>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        addressAdapter = AddressAdapter(addressList, this)
        rv_address_order.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_address_order.adapter = addressAdapter

        getListAddress()
        addEvent()
    }

    private fun getListAddress() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.ADDRESS)
            .child(user!!.uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    addressList.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.ADDRESS_ID).value as String
                        val phone = ds.child(PhysicsConstants.ADDRESS_PHONE).value as String
                        val name = ds.child(PhysicsConstants.ADDRESS_name).value as String
                        val address = ds.child(PhysicsConstants.ADDRESS_REAL).value as String
                        val default = ds.child(PhysicsConstants.ADDRESS_DEFAULT).value as Boolean
                        val addressObj = Address(id, name, phone, address, default)
                        addressList.add(addressObj)
                        Log.d("adress", id)
                        Log.d("adress", phone)
                        Log.d("adress", name)
                        Log.d("adress", address)
                        Log.d("adress", default.toString())
                    }
                    Log.d("sizemang123", addressList.size.toString())
                    addressAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    private fun addEvent() {
        tv_add_new_address.setOnClickListener() {
            showDialogAddNewAddress()
        }

        btn_address_continue.setOnClickListener() {

        }
    }

    //Theme_Light_NoTitleBar_Fullscreen
    fun showDialogAddNewAddress() {
        val dialog = Dialog(
            this, android.R.style.Theme_Light_NoTitleBar
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_new_address)

        // exit dialog
        dialog.btn_cancel_add_address.setOnClickListener { dialog.dismiss() }

        var name = ""
        var phone = ""
        var address = ""

        // check btn add thís address into list  address
        dialog.btn_add_address.setOnClickListener {
            name = dialog.edt_name_address.text.trim().toString()
            phone = dialog.edt_phone_address.text.trim().toString()
            address = dialog.edt_address.text.trim().toString()
            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_name_address.error = getString(R.string.error_input_name_not_entered)
            } else if (phone.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_phone_address.error = getString(R.string.error_input_phone_not_entered)
            } else if (isPhoneValid(phone) == false) {
                Toast.makeText(
                    this,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_phone_address.error =
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct)
            } else if (address.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_input_address_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_address.error = getString(R.string.error_input_address_not_entered)
            } else {
                val user: FirebaseUser? = mAuth?.getCurrentUser();
                // Check user loged in firebase yet?
                if (user != null) {
                    mDatabase = FirebaseDatabase.getInstance()
                    println("userid: " + user.uid)
                    val query = mDatabase!!
                        .reference
                        .child(PhysicsConstants.ADDRESS)
                        .child(user.uid)
                        .push()

                    val key = query.key
                    // add this address into address firebase using key = push() of firebase
                    val addressOj = Address(key, name, phone, address, false)
                    query.setValue(addressOj)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()

    }

    fun onClickQuiteAddress(view: View) {
        finish()
    }
}
