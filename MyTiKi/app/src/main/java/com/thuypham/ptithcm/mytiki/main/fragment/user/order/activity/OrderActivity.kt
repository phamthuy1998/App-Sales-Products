package com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity.CartActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.OrderAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Order
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.OrderDetail
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.order_activity.*

class OrderActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var orderAdapter: OrderAdapter? = null
    private var orderList = ArrayList<Order>()
    private var orderDetailList = ArrayList<OrderDetail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_activity)


        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        // product viewed init
        orderAdapter = OrderAdapter(orderList, orderDetailList, this)

        // Set rcyclerview horizontal
        rv_list_order.layoutManager = LinearLayoutManager(
            application,
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_list_order.adapter = orderAdapter

        var numViewMore = 0
        numViewMore = intent.getIntExtra("type_order", 0)

        getListOrder(numViewMore)

        addEvent()
        getCartCount()
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

        btn_continue_shopping_order.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            finish()
        }
    }

    // get cart count
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

    private fun getListOrderDetail(orderList: ArrayList<Order>) {
        if (orderList.isEmpty())
            ll_order_empty.visibility = View.VISIBLE
        else {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            val uid = user!!.uid
            for (o in orderList) {
                mDatabase = FirebaseDatabase.getInstance()
                val query = mDatabase!!
                    .reference
                    .child(PhysicsConstants.ORDER_DETAIL)
                    .child(uid)
                    .child(o.id.toString())

                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ll_order_empty.visibility = View.GONE
                            for (ds in dataSnapshot.children) {
                                if (ds.exists()) {
                                    val id =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_ID).value as String?
                                    val product_name =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_NAME).value as String?
                                    val id_product =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_ID_PRODUCT).value as String?
                                    val product_count =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_COUNT).value as Long?
                                    val product_price =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_PRICE).value as Long?
                                    val id_order =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_ID_ORDER).value as String?
                                    val image =
                                        ds.child(PhysicsConstants.ORDER_DETAIL_PRODUCT_IMAGE).value as String?

                                    if (id != null && image != null && product_name != null && id_product != null
                                        && product_count != null && product_price != null && id_order != null
                                    ) {
                                        orderDetailList.add(
                                            OrderDetail(
                                                id,
                                                product_name,
                                                id_product,
                                                image,
                                                product_count,
                                                product_price,
                                                id_order
                                            )
                                        )
                                    }
                                }
                            }
                            orderAdapter?.notifyDataSetChanged()
                        } else {
                            ll_order_empty.visibility = View.VISIBLE
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                }
                query.addValueEventListener(valueEventListener)
            }

        }
    }

    private fun getListOrder(numViewMore: Int) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user!!.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.ORDER)
                .child(uid)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ll_order_empty.visibility = View.GONE
                        orderList.clear()
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                val id = ds.child(PhysicsConstants.ORDER_ID).value as String?
                                val date = ds.child(PhysicsConstants.ORDER_DATE).value as String?
                                val price = ds.child(PhysicsConstants.ORDER_PRICE).value as Long?
                                val status = ds.child(PhysicsConstants.ORDER_STATUS).value as Long?

                                val phone = ds.child(PhysicsConstants.ADDRESS_PHONE).value as String
                                val name = ds.child(PhysicsConstants.ADDRESS_name).value as String
                                val address =
                                    ds.child(PhysicsConstants.ADDRESS_REAL).value as String
                                if (id != null && date != null && price != null && status != null) {
                                    if (numViewMore == 0) {
                                        orderList.add(
                                            Order(
                                                id,
                                                name,
                                                phone,
                                                address,
                                                date,
                                                price,
                                                status
                                            )
                                        )
                                    } else if (numViewMore == 1 && status == 1.toLong()) {
                                        orderList.add(
                                            Order(
                                                id,
                                                name,
                                                phone,
                                                address,
                                                date,
                                                price,
                                                status
                                            )
                                        )
                                    } else if (numViewMore == 2 && status == 2.toLong()) {
                                        orderList.add(
                                            Order(
                                                id,
                                                name,
                                                phone,
                                                address,
                                                date,
                                                price,
                                                status
                                            )
                                        )
                                    } else if (numViewMore == 3 && status == 3.toLong()) {
                                        orderList.add(
                                            Order(
                                                id,
                                                name,
                                                phone,
                                                address,
                                                date,
                                                price,
                                                status
                                            )
                                        )
                                    } else if (numViewMore == 4 && status == 4.toLong()) {
                                        orderList.add(
                                            Order(
                                                id,
                                                name,
                                                phone,
                                                address,
                                                date,
                                                price,
                                                status
                                            )
                                        )
                                    }

                                }
                            }
                        }
                        if (orderList.isEmpty())
                            ll_order_empty.visibility = View.VISIBLE
                        else {
                            orderList.reverse()
                            getListOrderDetail(orderList)
                        }
                        orderAdapter?.notifyDataSetChanged()

                    } else {
                        ll_order_empty.visibility = View.VISIBLE
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

    fun onClickBackToUser(view: View) {
        finish()
    }
}