package com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.help.isPhoneValid
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCartDetail
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.AddressAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.ProductConfirmAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Address
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Order
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.OrderDetail
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.dialog_add_new_address.*
import kotlinx.android.synthetic.main.dialog_cofirm_order.*
import kotlinx.android.synthetic.main.dialog_order_success.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddressActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //product
    private var addressAdapter: AddressAdapter? = null
    private var addressList = ArrayList<Address>()
    private lateinit var currentAddress: Address

    var productList = ArrayList<ProductCartDetail>()
    private var productAdapter: ProductConfirmAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        addressAdapter =
            AddressAdapter(
                addressList,
                this
            )
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
                        val addressObj =
                            Address(
                                id,
                                name,
                                phone,
                                address,
                                default
                            )
                        addressList.add(addressObj)
                        Log.d("adress", id)
                        Log.d("adress", phone)
                        Log.d("adress", name)
                        Log.d("adress", address)
                        Log.d("adress", default.toString())
                    }
                    Log.d("sizemang123", addressList.size.toString())
                    addressList[0].default = true
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
            showDialogConfirmOrder()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun showDialogConfirmOrder() {
        val dialog = Dialog(
            this, android.R.style.Theme_Light_NoTitleBar
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_cofirm_order)

        // set address
        for (address in addressList) {
            if (address.default == true)
                currentAddress = address
        }
        dialog.tv_name_address_conf.text = currentAddress.name
        dialog.tv_phone_conf.text = currentAddress.phone
        dialog.tv_address_conf.text = currentAddress.address

        // get list cart
        productList = intent.getParcelableArrayListExtra<ProductCartDetail>("listProductCart")
        productAdapter =
            ProductConfirmAdapter(
                productList,
                this
            )
        // Set rcyclerview vertial
        dialog.rv_product_confirm.layoutManager = LinearLayoutManager(
            application,
            LinearLayoutManager.VERTICAL,
            false
        )
        dialog.rv_product_confirm.adapter = productAdapter


        var priceTemp = 0.0
        for (p in productList) {
            priceTemp += p.price!!.minus(((p.sale * 0.01) * p.price!!)) * p.number_product!!
        }
        // format price
        val df = DecimalFormat("#,###,###")
        df.roundingMode = RoundingMode.CEILING
        var priceTxt = df.format(priceTemp) + " đ"
        dialog.tv_price_temp_order.text = priceTxt

        var priceAmount = priceTemp

        if (priceTemp > 100000) {
            dialog.tv_price_shipping.text = "0 đ"
        } else {
            priceAmount += PhysicsConstants.Shipping
            priceTxt = df.format(PhysicsConstants.Shipping) + " đ"
            dialog.tv_price_shipping.text = priceTxt
        }

        priceTxt = df.format(priceAmount) + " đ"
        dialog.tv_price_cart_conf.text = priceTxt

        // exit dialog
        dialog.btn_cancel_confirm.setOnClickListener { dialog.dismiss() }

        // add in oerder
        dialog.btn_payment_conf.setOnClickListener {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            // Check user loged in firebase yet?
            if (user != null) {
                mDatabase = FirebaseDatabase.getInstance()
                var query = mDatabase!!
                    .reference
                    .child(PhysicsConstants.ORDER)
                    .child(user.uid)
                    .push()

                val key = query.key
                // add order into
                val current = LocalDateTime.now()
                val dateFormatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")
                val dateFormatted = current.format(dateFormatter)

//                id, name, phone, address, date, price, status
                val order = Order(
                    key,
                    currentAddress.name,
                    currentAddress.phone,
                    currentAddress.address,
                    dateFormatted,
                    priceAmount.toLong(),
                    1
                )
                query.setValue(order)


                for (p in productList) {
                    query = mDatabase!!
                        .reference
                        .child(PhysicsConstants.ORDER_DETAIL)
                        .child(user.uid)
                        .child(key.toString())
                        .push()

                    val keyDetail = query.key
                    query.setValue(
                        OrderDetail(
                            keyDetail,
                            p.name,
                            p.id,
                            p.image,
                            p.number_product,
                            p.price,
                            key
                        )
                    )
                }

                delAllCartOfUser()

                dialog.dismiss()
                showDialogInforOrder()
            }
        }

        dialog.show()

    }

    // Delete all cart of user
    private fun delAllCartOfUser() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        mDatabaseReference = mDatabase!!.reference
        val currentUserDb = mDatabaseReference.child(PhysicsConstants.CART)
            .child(user!!.uid)
            .removeValue()
    }

    private fun showDialogInforOrder() {
        val dialog = Dialog(
            this, android.R.style.Theme_Light_NoTitleBar
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_order_success)

        // exit dialog
        dialog.btn_cancel_dialog_success_order.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            finish()
        }
        dialog.btn_continue_shopping_dialog.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            finish()
        }
        dialog.btn_view_order.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("type_order", 0)
            startActivity(intent)
        }
        dialog.show()
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
                    val addressOj =
                        Address(
                            key,
                            name,
                            phone,
                            address,
                            false
                        )
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
