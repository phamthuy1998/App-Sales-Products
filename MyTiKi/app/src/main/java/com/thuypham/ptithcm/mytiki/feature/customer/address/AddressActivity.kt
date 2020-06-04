package com.thuypham.ptithcm.mytiki.feature.customer.address

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sg.vivastory.ext.getTxtTrim
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Address
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.OrderDetail
import com.thuypham.ptithcm.mytiki.data.ProductCartDetail
import com.thuypham.ptithcm.mytiki.ext.AppExecutors
import com.thuypham.ptithcm.mytiki.ext.Credentials
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.customer.address.adapter.AddressAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.customer.order.OrderActivity
import com.thuypham.ptithcm.mytiki.feature.customer.order.adapter.ProductConfirmAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.getRandomString
import com.thuypham.ptithcm.mytiki.util.isPhoneValid
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.dialog_add_new_address.*
import kotlinx.android.synthetic.main.dialog_cofirm_order.*
import kotlinx.android.synthetic.main.dialog_confirm_email.*
import kotlinx.android.synthetic.main.dialog_order_success.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class AddressActivity : AppCompatActivity() {

    private lateinit var appExecutors: AppExecutors
    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    //product
    private var addressAdapter: AddressAdapter? = null
    private var addressList = ArrayList<Address>()
    private lateinit var currentAddress: Address

    var productList = ArrayList<ProductCartDetail>()
    private var productAdapter: ProductConfirmAdapter? = null

    private var codeEmail = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appExecutors = AppExecutors()


        setContentView(R.layout.activity_address)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(Constant.USER)

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

        val nameTb = intent.getStringExtra("userAddress")
        if (nameTb != null) {
            btn_address_continue.visibility = View.GONE
            tv_tb_address.setText(nameTb)
        }

        getListAddress()
        addEvent()
    }

    private fun getListAddress() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        val query = mDatabase!!
            .reference
            .child(Constant.ADDRESS)
            .child(user!!.uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    addressList.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(Constant.ADDRESS_ID).value as String
                        val phone = ds.child(Constant.ADDRESS_PHONE).value as String
                        val name = ds.child(Constant.ADDRESS_name).value as String
                        val address = ds.child(Constant.ADDRESS_REAL).value as String
                        val default = ds.child(Constant.ADDRESS_DEFAULT).value as Boolean
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
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    private fun addEvent() {
        tv_add_new_address.setOnClickListener() {
            showDialogAddNewAddress()
        }

        btn_address_continue.setOnClickListener() {
            if (addressList.isEmpty()) {
                Toast.makeText(this, R.string.err_address_empty, Toast.LENGTH_LONG).show()
            } else {
                val randomStr = getRandomString(6)
                val email = mAuth?.currentUser?.email?.let { it1 ->
                    progressAddress.visible()
                    sendEmail(it1, randomStr)

                }
            }
        }
    }

    private fun sendEmail(emailReceive:String,randomStr :String){
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session =  Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                //Setting sender address
                mm.setFrom(InternetAddress(Credentials.EMAIL))
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, InternetAddress(emailReceive))
                //Adding subject
                mm.subject = getString(R.string.confirmOrder)
                //Adding message
                mm.setContent("<html><body><h1>${getString(R.string.emailContent)}$randomStr</h1></body></html>",
                    "text/html; charset=utf-8")

                //Sending email
                Transport.send(mm)

                appExecutors.mainThread().execute {
                    progressAddress.gone()
                    showDialogSendmail(randomStr)
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }


    private fun showDialogSendmail(randomStr: String) {
        val dialog = Dialog(
            this, Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_confirm_email)

        dialog.btnConfirm.setOnClickListener {
            if (dialog.edtCode.getTxtTrim() == "") {
                dialog.edtCode.error = getString(R.string.errCode)
                return@setOnClickListener
            }
            if (randomStr == dialog.edtCode.getTxtTrim()) {
                dialog.dismiss()
                showDialogConfirmOrder()
            } else {
                dialog.edtCode.error = getString(R.string.errCodeIncorrect)
                Toast.makeText(this, getString(R.string.errCodeIncorrect), Toast.LENGTH_LONG).show()
            }
        }
        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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
            priceTemp += p.price!!.minus(((p.sale?.times(0.01))?.times(p.price!!)!!)) * p.number_product!!
        }
        // format price
        val df = DecimalFormat("#,###,###")
        df.roundingMode = RoundingMode.CEILING
        val priceTxt = df.format(priceTemp) + " đ"
        dialog.tv_price_cart_conf.text = priceTxt

        // exit dialog
        dialog.btn_cancel_confirm.setOnClickListener { dialog.dismiss() }

        // add in oerder
        dialog.btn_payment_conf.setOnClickListener {
            val user = mAuth?.currentUser;
            // Check user loged in firebase yet?
            if (user != null) {
                mDatabase = FirebaseDatabase.getInstance()
                var query = mDatabase!!
                    .reference
                    .child(Constant.ORDER)
                    .push()

                val key = query.key
                // add order into
                val current = LocalDateTime.now()
                val dateFormatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")
                val dateSearchFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateFormatted = current.format(dateFormatter)
                val dateSearch = current.format(dateSearchFormatter)

//                id, name, phone, address, date, price, status
                val order = Order(
                    key,
                    currentAddress.name,
                    user.uid,
                    currentAddress.phone,
                    currentAddress.address,
                    dateFormatted,
                    dateSearch,
                    priceTemp.toLong(),
                    1
                )
                query.setValue(order)


                for (p in productList) {
                    query = mDatabase!!
                        .reference
                        .child(Constant.ORDER_DETAIL)
                        .push()

                    val keyDetail = query.key
                    query.setValue(
                        OrderDetail(
                            keyDetail,
                            p.name,
                            p.id,
                            p.image,
                            p.number_product,
                            p.price?.minus((p.price ?: 0) * 0.01 * (p.sale ?: 0))?.toLong(),
                            key
                        )
                    )
                }

                delAllCartOfUser()

                dialog.dismiss()
                showDialogInfoOrder()
            }
        }

        dialog.show()

    }

    // Delete all cart of user
    private fun delAllCartOfUser() {
        val user: FirebaseUser? = mAuth?.currentUser;
        mDatabaseReference = mDatabase!!.reference
        val currentUserDb = mDatabaseReference.child(Constant.CART)
            .child(user!!.uid)
            .removeValue()
    }

    private fun showDialogInfoOrder() {
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
            dialog.dismiss()
            finish()
        }
        dialog.btn_continue_shopping_dialog.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            dialog.dismiss()
            finish()
        }
        dialog.btn_view_order.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("type_order", 0)
            dialog.dismiss()
            startActivity(intent)
        }
        dialog.show()
    }

    //Theme_Light_NoTitleBar_Fullscreen
    private fun showDialogAddNewAddress() {
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
            name = dialog.edt_name_address.text?.trim().toString()
            phone = dialog.edt_phone_address.text?.trim().toString()
            address = dialog.edt_address.text?.trim().toString()
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
                        .child(Constant.ADDRESS)
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
