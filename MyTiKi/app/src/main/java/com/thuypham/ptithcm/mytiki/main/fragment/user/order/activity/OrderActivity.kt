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
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter.OrderAdapter
import com.thuypham.ptithcm.mytiki.main.product.model.Order
import com.thuypham.ptithcm.mytiki.main.product.model.OrderDetail
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
        getListOrderDetail()
    }

    private fun getListOrderDetail() {
        if (orderList.isEmpty())
            ll_order_empty.visibility = View.VISIBLE
        else {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            val uid = user!!.uid
            mDatabase = FirebaseDatabase.getInstance()

            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.ORDER_DETAIL)
                .child(uid)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ll_order_empty.visibility = View.GONE
                        orderDetailList.clear()
                        for (ds in dataSnapshot.children) {
                            if (ds.exists()) {
                                val id = ds.child(PhysicsConstants.ORDER_DETAIL_ID).value as String?
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
                                if (id != null
                                    && product_name != null
                                    && id_product != null
                                    && product_count != null
                                    && product_price != null
                                    && id_order != null
                                ) {
                                    orderDetailList.add(
                                        OrderDetail(
                                            id,
                                            product_name,
                                            id_product,
                                            product_count,
                                            product_price,
                                            id_order
                                        )
                                    )
                                }
                            }
                        }
                        Log.d("sizemangac", orderDetailList.size.toString())
                        orderAdapter?.notifyDataSetChanged()
                    } else {
                        ll_order_empty.visibility = View.VISIBLE
                        println("k co dl favorite viewed")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }

    private fun getListOrder(numViewMore: Int) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            val uid = user!!.uid
            println("user id: $uid")
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
                                if (id != null && date != null && price != null && status != null) {
                                    if (numViewMore == 0) {
                                        orderList.add(Order(id, date, price, status))
                                    } else if (numViewMore == 1 && status == 1.toLong()) {
                                        orderList.add(Order(id, date, price, status))
                                    } else if (numViewMore == 2 && status == 2.toLong()) {
                                        orderList.add(Order(id, date, price, status))
                                    } else if (numViewMore == 3 && status == 3.toLong()) {
                                        orderList.add(Order(id, date, price, status))
                                    } else if (numViewMore == 4 && status == 4.toLong()) {
                                        orderList.add(Order(id, date, price, status))
                                    }

                                }
                            }
                        }
                        if (orderList.isEmpty()) ll_order_empty.visibility = View.VISIBLE
                        orderAdapter?.notifyDataSetChanged()

                    } else {
                        ll_order_empty.visibility = View.VISIBLE
                        println("k co dl favorite viewed")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
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