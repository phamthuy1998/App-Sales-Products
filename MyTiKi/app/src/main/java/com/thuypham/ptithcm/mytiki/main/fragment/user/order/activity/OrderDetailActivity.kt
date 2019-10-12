package com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity.CartActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCartDetail
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.ProductConfirmAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.ProductOrderAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Order
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.OrderDetail
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.order_activity.*
import java.math.RoundingMode
import java.text.DecimalFormat

class OrderDetailActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private lateinit var order: Order

    var productList = ArrayList<OrderDetail>()
    private var productAdapter: ProductOrderAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference

        val id_order = intent.getStringExtra("order_id")
        if (id_order != null) {
            productAdapter =
                ProductOrderAdapter(
                    productList,
                    this
                )
            // Set rcyclerview vertial
            rv_product_order_detail.layoutManager = LinearLayoutManager(
                application,
                LinearLayoutManager.VERTICAL,
                false
            )
            rv_product_order_detail.adapter = productAdapter
            getInforOrderDetail(id_order)
            getListOrderProduct(id_order)
            addEvent()
            getCartCount()
        }
    }

    private fun addEvent() {

        ll_cart_number.setOnClickListener() {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            if (user != null) {
                val intentCart = Intent(this, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(this, SignInUpActivity::class.java)
                startActivity(intentCart)
            }
        }

        btn_cancel_order_detail.setOnClickListener() {
            cancelOrder()
        }
    }

    private fun getCartCount() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user!!.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.CART)
                .child(uid)
            var cartCount = 0

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cartCount = 0
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                cartCount++
                            }
                        }
                        if (cartCount > 0 && tv_number_cart != null) {
                            tv_number_cart.visibility = View.VISIBLE
                            tv_number_cart.text = cartCount.toString()
                        } else if (tv_number_cart != null) {
                            tv_number_cart.visibility = View.GONE
                        }
                    } else if (tv_number_cart != null) {
                        tv_number_cart.visibility = View.GONE
                        cartCount = 0
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        } else {
            tv_number_cart.visibility = View.GONE
        }
    }

    private fun cancelOrder() {

    }

    private fun getInforOrderDetail(id_order: String) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.ORDER)
                .child(uid)
                .child(id_order)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        val id = ds.child(PhysicsConstants.ORDER_ID).value as String?
                        val date = ds.child(PhysicsConstants.ORDER_DATE).value as String?
                        val price = ds.child(PhysicsConstants.ORDER_PRICE).value as Long?
                        val status = ds.child(PhysicsConstants.ORDER_STATUS).value as Long?

                        val phone = ds.child(PhysicsConstants.ADDRESS_PHONE).value as String
                        val name = ds.child(PhysicsConstants.ADDRESS_name).value as String
                        val address = ds.child(PhysicsConstants.ADDRESS_REAL).value as String
                        if (id != null) {
                            order =
                                Order(
                                    id,
                                    name,
                                    phone,
                                    address,
                                    date,
                                    price,
                                    status
                                )
                            setInfororder(order)
                        }
                    } else {
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)

        }
    }

    private fun setInfororder(order: Order) {
        tv_item_order_detail_ac_id.text = order.id
        tv_item_order_detail_date.text = order.date
        var status = ""
        if (order.status == 1.toLong()) {
            status = getString(R.string.status_1)
        }
        // shipping
        else if (order.status == 2.toLong()) {
            status = getString(R.string.status_2)
        }
        // order success
        else if (order.status == 3.toLong()) {
            status = getString(R.string.status_3)
        }
        // order cancel
        else if (order.status == 4.toLong()) {
            status = getString(R.string.status_4)
        }
        tv_item_order_detail_status.text = status
        tv_name_address_order_detail.text = order.name
        tv_phone_order_detail.text = order.phone
        tv_address_order_detail.text = order.address
        //format price
        val df = DecimalFormat("#,###,###")
        df.roundingMode = RoundingMode.CEILING
        var price = 0.00.toLong()
        price = order.price!!
        if (order.price!! >= (PhysicsConstants.PriceLeast + PhysicsConstants.Shipping)) {
            val priceTxt = df.format(price) + " đ"
            tv_price_shipping_order_detail.text = "0 đ"
            tv_price_temp_order_detail.text = priceTxt
            tv_price_amount_order_detail.text = priceTxt
        } else {
            var priceTxt = df.format(order.price!! - PhysicsConstants.Shipping) + " đ"
            tv_price_temp_order_detail.text = priceTxt
            priceTxt = df.format(PhysicsConstants.Shipping) + " đ"
            tv_price_shipping_order_detail.text = priceTxt
            priceTxt = df.format(price) + " đ"
            tv_price_amount_order_detail.text = priceTxt
        }

    }

    private fun getListOrderProduct(orderId: String) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.ORDER_DETAIL)
                .child(uid)
                .child(orderId)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        productList.clear()
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                val id = ds.child(PhysicsConstants.ORDER_DETAIL_ID).value as String?
                                val product_name =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_NAME).value as String?
                                val id_product =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_ID_PRODUCT).value as String?
                                val image_product =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_IMAGE).value as String?
                                val product_count =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_COUNT).value as Long?
                                val product_price =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_PRICE).value as Long?
                                val id_order =
                                    ds.child(PhysicsConstants.ORDER_DETAIL_ID_ORDER).value as String?
                                if (id != null && product_name != null && id_product != null && image_product != null
                                    && product_count != null && product_price != null && id_order != null
                                ) {
                                    productList.add(
                                        OrderDetail(
                                            id,
                                            product_name,
                                            id_product,
                                            image_product,
                                            product_count,
                                            product_price,
                                            id_order
                                        )
                                    )
                                }
                            }
                        }
                        productAdapter?.notifyDataSetChanged()

                    } else {
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        } else {
            val intent = Intent(this, SignInUpActivity::class.java)
            startActivity(intent)
        }
    }


    fun onClickCancelOrderDetail(view: View) {
        finish()
    }
}
