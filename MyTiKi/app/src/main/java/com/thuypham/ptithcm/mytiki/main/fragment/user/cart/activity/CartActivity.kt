package com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.adapter.ProductCartAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCart
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCartDetail
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity.AddressActivity
import kotlinx.android.synthetic.main.activity_cart.*
import java.math.RoundingMode
import java.text.DecimalFormat

class CartActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductCart = ArrayList<ProductCart>()
    private var productCartAdapter: ProductCartAdapter? = null
    private var productCartList = ArrayList<ProductCartDetail>()
    private var priceCart: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        val user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            // product cart init
            productCartAdapter =
                ProductCartAdapter(
                    productCartList,
                    this
                )
            // Set rcyclerview vertical
            rv_product_cart.layoutManager = LinearLayoutManager(
                application,
                LinearLayoutManager.VERTICAL,
                false
            )
            rv_product_cart.adapter = productCartAdapter

            tv_tb_cart_title.text = getString(R.string.cart) + "(" + productCartList.size + ")"

            getListCart()

            addEvent()

        } else {
            var intent = Intent(this, SignInUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addEvent() {
        // If list product cart is empty, click button continue to shopping, intent to main activity
        btn_continue_shopping_cart.setOnClickListener() {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btn_order_cart.setOnClickListener() {
            var intent = Intent(this, AddressActivity::class.java)
            intent.putParcelableArrayListExtra("listProductCart", productCartList)
            startActivity(intent)
        }
    }


    // get list product id and  product number into cart
    private fun getListCart() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        val uid = user!!.uid
        println("user id: $uid")
        mDatabase = FirebaseDatabase.getInstance()

        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.CART)
            .child(uid)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    ll_list_cart_empty.visibility = View.GONE
                    arrIdProductCart.clear()
                    println("viewed favorite product co du lieu")
                    for (ds in dataSnapshot.children) {
                        if (ds.exists()) {
                            val id: String? = ds.child(PhysicsConstants.CART_ID).value as String?
                            val cart_number = ds.child(PhysicsConstants.CART_NUMBER).value as Long?
                            println(" id product: $id")
                            if (id != null && cart_number != null) {
                                arrIdProductCart.add(
                                    ProductCart(
                                        id,
                                        cart_number
                                    )
                                )
                            }
                        }
                    }
                    // get product viewed infor
                    if (!arrIdProductCart.isEmpty()) {
                        println("list cart khong rong")
                        productCartAdapter?.notifyDataSetChanged()
                        getListProductByID(arrIdProductCart)
                    } else {
                        arrIdProductCart.clear()
                        productCartAdapter?.notifyDataSetChanged()
                    }

                } else {
                    tv_price_cart.text = "0 đ"
                    ll_list_cart_empty.visibility = View.VISIBLE
                    println("k co dl favorite viewed")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        query.addValueEventListener(valueEventListener)

    }

    // From list product id, then get all infor
    fun getListProductByID(arrProductCartId: ArrayList<ProductCart>) {
        var product: ProductCartDetail
        var priceCart = 0.00
        productCartList.clear()
        for (productCart in arrProductCartId) {
            println("vo toi day luon nhi")
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.PRODUCT)
                .child(productCart.id!!)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        println("co vo day lay thong tin k")
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        val price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long

                        println("name of product : $name")
                        product =
                            ProductCartDetail(
                                productCart.id,
                                name,
                                price,
                                productCart.product_count,
                                image,
                                product_count,
                                id_category,
                                sale
                            )

                        priceCart += price.minus(((sale * 0.01) * price)) * productCart.product_count

                        // format price viewed
                        val df = DecimalFormat("#,###,###")
                        df.roundingMode = RoundingMode.CEILING
                        val priceTxt = df.format(priceCart) + " đ"
                        tv_price_cart.text = priceTxt

                        productCartList.add(product)
                        println("size mang cart:" + productCartList.size)
                        productCartAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("lay 1 sp k thanh cong")
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }


    fun onClickQuiteCart(view: View) {
        finish()
    }
}
