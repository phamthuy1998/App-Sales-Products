package com.thuypham.ptithcm.mytiki.feature.customer.order.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.BaseItem
import com.thuypham.ptithcm.mytiki.feature.customer.order.model.Address
import com.thuypham.ptithcm.mytiki.util.PhysicsConstants
import com.thuypham.ptithcm.mytiki.util.isPhoneValid
import kotlinx.android.synthetic.main.dialog_add_new_address.*
import kotlinx.android.synthetic.main.item_address.view.*

class AddressAdapter(
    private var items: ArrayList<Address>,
    private val context: Context
) : RecyclerView.Adapter<BaseItem>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_address, viewGroup, false);
        return ProductViewholder(view)
    }

    inner class ProductViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val address = items[position]
            if (address != null) {
                itemView.tv_name_address.text = address.name.toString()
                itemView.tv_phone_address.text = address.phone.toString()
                itemView.tv_real_address.text = address.address.toString()
                if (address.default == true)
                    itemView.rad_defaul_address.isChecked = true
                else
                    itemView.rad_defaul_address.isChecked = false

                //del address
                itemView.btn_del_address.setOnClickListener() {
                    delAddressFromListAddress(address)
                }

                //edit address
                itemView.btn_edit_address.setOnClickListener() {
                    editAddress(address)
                }

                // choose this address to use it to address defaul
                itemView.ll_item_address.setOnClickListener() {
                    for (addressObj in items) {
                        addressObj.default = false
                    }
                    address.default = true
                    itemView.rad_defaul_address.isChecked = true
                    notifyDataSetChanged()
                }
            }
        }

    }


    private fun editAddress(addressObj: Address) {

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.getCurrentUser();
        val uid = user!!.uid
        var mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
        var mDatabaseReference: DatabaseReference = mDatabase!!.reference

        val dialog = Dialog(
            context, android.R.style.Theme_Light_NoTitleBar
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_add_new_address)

        //dialog set infor address
        dialog.edt_name_address.setText(addressObj.name.toString())
        dialog.edt_phone_address.setText(addressObj.phone.toString())
        dialog.edt_address.setText(addressObj.address.toString())

        // exit dialog
        dialog.btn_cancel_add_address.setOnClickListener { dialog.dismiss() }

        dialog.btn_add_address.setText(R.string.update)

        var name = ""
        var phone = ""
        var address = ""

        // check btn add thís address into list  address
        dialog.btn_add_address.setOnClickListener {
            name = dialog.edt_name_address.text?.trim().toString()
            phone = dialog.edt_phone_address.text?.trim().toString()
            address = dialog.edt_address.text?.trim().toString()
            if (name.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(com.thuypham.ptithcm.mytiki.R.string.error_input_name_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_name_address.error =
                    context.getString(R.string.error_input_name_not_entered)
            } else if (phone.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_phone_address.error =
                    context.getString(R.string.error_input_phone_not_entered)
            } else if (isPhoneValid(phone) == false) {
                Toast.makeText(
                    context,
                    context.getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_phone_address.error =
                    context.getString(com.thuypham.ptithcm.mytiki.R.string.error_input_phone_not_correct)
            } else if (address.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(com.thuypham.ptithcm.mytiki.R.string.error_input_address_not_entered),
                    Toast.LENGTH_LONG
                ).show()
                dialog.edt_address.error =
                    context.getString(R.string.error_input_address_not_entered)
            } else {
                val user: FirebaseUser? = mAuth?.getCurrentUser();
                // Check user loged in firebase yet?
                if (user != null) {
                    //del this address
                    delAddressFromListAddress(addressObj)

                    // then add another address
                    mDatabase = FirebaseDatabase.getInstance()
                    val query = mDatabase!!
                        .reference
                        .child(PhysicsConstants.ADDRESS)
                        .child(user.uid)
                        .child(addressObj.id!!)

                    // add this address into address firebase using key = push() of firebase
                    val addressOj =
                        Address(
                            addressObj.id!!,
                            name,
                            phone,
                            address,
                            false
                        )
                    query.setValue(addressOj)
                    dialog.dismiss()

                    // update view
                    notifyDataSetChanged()
                }
            }
        }
        dialog.show()
    }

    // del address
    private fun delAddressFromListAddress(address: Address) {
        if (address.default != true) {
            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val user: FirebaseUser? = mAuth.getCurrentUser();
            val uid = user!!.uid
            var mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
            var mDatabaseReference: DatabaseReference = mDatabase!!.reference
            if (address.id != null) {
                val currentUserDb = mDatabaseReference.child(PhysicsConstants.ADDRESS)
                    .child(user.uid)
                    .child(address.id!!)

                if (currentUserDb != null) {
                    currentUserDb.removeValue()
                    items.remove(address)
                    notifyDataSetChanged()
                }

            }
        } else {
            Toast.makeText(context, R.string.err_del_address, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItem, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}