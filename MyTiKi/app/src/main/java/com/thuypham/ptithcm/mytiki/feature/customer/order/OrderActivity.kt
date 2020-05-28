package com.thuypham.ptithcm.mytiki.feature.customer.order

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
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.customer.order.adapter.OrderAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
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
        mDatabaseReference = mDatabase!!.reference.child(Constant.USER)

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
            val user: FirebaseUser? = mAuth?.currentUser;
            if (user != null) {
                val intentCart = Intent(this, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(this, AuthActivity::class.java)
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
        val user: FirebaseUser? = mAuth?.currentUser;
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

    private fun getListOrderDetail(orderList: ArrayList<Order>) {
        if (orderList.isEmpty())
            ll_order_empty.visibility = View.VISIBLE
        else {
            for (o in orderList) {
                mDatabase = FirebaseDatabase.getInstance()
                val query = mDatabase!!
                    .reference
                    .child(Constant.ORDER_DETAIL)
                    .orderByChild(Constant.ORDER_DETAIL_ID_ORDER)
                    .equalTo(o.id.toString())

                var orderDetail: OrderDetail?
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ll_order_empty.visibility = View.GONE
                            for (ds in dataSnapshot.children) {
                                orderDetail = ds.getValue(OrderDetail::class.java)
                                orderDetail?.let { orderDetailList.add(it) }
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
        val user: FirebaseUser? = mAuth?.currentUser;
        if (user != null) {
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(Constant.ORDER)
                .orderByChild(Constant.ORDER_ID_USER).equalTo(uid)
            var order: Order?
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ll_order_empty.visibility = View.GONE
                        orderList.clear()
                        for (ds in dataSnapshot.children) {
                            order = ds.getValue(Order::class.java)
                            if (numViewMore == 0) {
                                order?.let { orderList.add(it) }
                            } else if (numViewMore == 1 && order?.status == 1) {
                                order?.let { orderList.add(it) }
                            } else if (numViewMore == 2 && order?.status == 2) {
                                order?.let { orderList.add(it) }
                            } else if (numViewMore == 3 && order?.status == 3) {
                                order?.let { orderList.add(it) }
                            } else if (numViewMore == 4 && order?.status == 4) {
                                order?.let { orderList.add(it) }
                            }

                        }
                        if (orderList.isEmpty())
                            ll_order_empty.visibility = View.VISIBLE
                        else {
                            orderList.reverse()
                            getListOrderDetail(orderList)
                        }
                        orderAdapter?.notifyDataSetChanged()
                    } else
                        ll_order_empty.visibility = View.VISIBLE
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

    fun onClickBackToUser(view: View) {
        finish()
    }
}