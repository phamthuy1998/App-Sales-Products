package com.thuypham.ptithcm.mytiki.feature.customer.order

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.OrderDetail
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.order.adapter.ProductOrderAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.ll_cart.*
import java.math.RoundingMode
import java.text.DecimalFormat

class OrderDetailActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var order: Order? = null

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

            getInfoOrderDetail(id_order)
            getListOrderProduct(id_order)
            addEvent()
            getCartCount()
        }
    }

    private fun addEvent() {

        ll_cart_number.setOnClickListener() {
            val user: FirebaseUser? = mAuth?.currentUser;
            if (user != null) {
                val intentCart = Intent(this, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(this, AuthActivity::class.java)
                startActivity(intentCart)
            }
        }
    }

    private fun getCartCount() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(Constant.CART)
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

    private fun cancelOrder(orderId: String?) {
        val user: FirebaseUser? = mAuth?.currentUser;
        if (user != null) {
            if (orderId != null) {
                mDatabase!!.reference
                    .child(Constant.ORDER)
                    .child(orderId)
                    .child(Constant.ORDER_STATUS)
                    .setValue(4)
                finish()
            }
        }
    }

    private fun getInfoOrderDetail(id_order: String) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(Constant.ORDER)
                .child(id_order)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        order = ds.getValue(Order::class.java)
                        order?.let { setInfoOrder(it) }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }

    private fun setInfoOrder(order: Order) {
        tv_item_order_detail_ac_id.text = order.id
        tv_item_order_detail_date.text = order.date
        var status = ""
        if (order.status == 1) {
            status = getString(R.string.status_1)
        }
        // shipping
        else if (order.status == 2) {
            status = getString(R.string.status_2)
        }
        // order success
        else if (order.status == 3) {
            status = getString(R.string.status_3)
        }
        // order cancel
        else if (order.status == 4) {
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
////        if (order.price!! >= (Constant.PriceLeast + Constant.Shipping)) {
//            val priceTxt = df.format(price) + " đ"
//            tv_price_shipping_order_detail.text = "0 đ"
//            tv_price_temp_order_detail.text = priceTxt
//            tv_price_amount_order_detail.text = priceTxt
//        } else {
            val priceTxt = df.format(order.price?:0) + " đ"
            tv_price_amount_order_detail.text = priceTxt
//        }

        // set button cancel show or hide
        if (order.status == 1) {
            btn_cancel_order_detail.visibility = View.VISIBLE
        } else {
            btn_cancel_order_detail.visibility = View.GONE
        }
        // add event for button cancel order
        btn_cancel_order_detail.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setMessage(getString(R.string.dialogCancelOrder))
                setPositiveButton(getString(R.string.dialogOk)) { dialog, id ->
                    dialog.dismiss()
                    cancelOrder(order.id)
                }
                setNegativeButton(getString(R.string.dialogCancel)) { dialog, id ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    private fun getListOrderProduct(orderId: String) {
        val user: FirebaseUser? = mAuth?.currentUser;
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(Constant.ORDER_DETAIL)
                .orderByChild(Constant.ORDER_DETAIL_ID_ORDER)
                .equalTo(orderId)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        productList.clear()
                        var orderDetail: OrderDetail?
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                orderDetail = ds.getValue(OrderDetail::class.java)
                                orderDetail?.let { productList.add(it) }
                            }
                        }
                        productAdapter?.notifyDataSetChanged()

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        } else {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }


    fun onClickCancelOrderDetail(view: View) {
        finish()
    }
}
