package com.thuypham.ptithcm.mytiki.feature.customer.cart

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.util.PhysicsConstants
import com.thuypham.ptithcm.mytiki.feature.customer.cart.adapter.ProductCartAdapter
import com.thuypham.ptithcm.mytiki.data.ProductCart
import com.thuypham.ptithcm.mytiki.data.ProductCartDetail
import com.thuypham.ptithcm.mytiki.feature.authentication.SignInUpActivity
import com.thuypham.ptithcm.mytiki.feature.customer.order.activity.AddressActivity
import kotlinx.android.synthetic.main.activity_cart.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CartActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductCart = ArrayList<ProductCart>()
    private var productCartAdapter: ProductCartAdapter? = null
    private var productCartList = ArrayList<ProductCartDetail>()


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
        mDatabase = FirebaseDatabase.getInstance()

        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.CART)
            .child(uid)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    ll_list_cart_empty.visibility = View.GONE
                    arrIdProductCart.clear()
                    for (ds in dataSnapshot.children) {
                        if (ds.exists()) {
                            val id: String? = ds.child(PhysicsConstants.CART_ID).value as String?
                            val cart_number = ds.child(PhysicsConstants.CART_NUMBER).value as Long?
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
                        productCartAdapter?.notifyDataSetChanged()
                        getListProductByID(arrIdProductCart)
                    } else {
                        arrIdProductCart.clear()
                        productCartAdapter?.notifyDataSetChanged()
                    }

                } else {
                    tv_price_cart.text = "0 đ"
                    ll_list_cart_empty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    // From list product id, then get all infor
    @RequiresApi(Build.VERSION_CODES.O)
    fun getListProductByID(arrProductCartId: ArrayList<ProductCart>) {
        var product: ProductCartDetail
        var priceCart = 0.00
        productCartList.clear()
        val current = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("HH")
        val hours = current.format(dateFormatter).toLong()
        for (productCart in arrProductCartId) {
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.PRODUCT)
                .child(productCart.id!!)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        var price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        if (hours in 7..10)
                            price *= PhysicsConstants.coefficientMorning
                        else if (hours in 11..12)
                            price = (price * PhysicsConstants.coefficientLunch).toLong()
                        else if (hours in 13..17)
                            price = (price * PhysicsConstants.coefficientAft).toLong()
                        else if (hours in 18..23)
                            price = (price * PhysicsConstants.coefficientNight).toLong()
                        else if (hours in 0..6)
                            price = (price * PhysicsConstants.coefficientMidNight).toLong()
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long

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
                        productCartAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }


    fun onClickQuiteCart(view: View) {
        finish()
    }
}
