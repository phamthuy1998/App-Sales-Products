package com.thuypham.ptithcm.mytiki.feature.customer.product

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.util.PhysicsConstants
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.authentication.SignInUpActivity
import com.thuypham.ptithcm.mytiki.feature.customer.product.adapter.ProductDetailApdater
import com.thuypham.ptithcm.mytiki.data.Product
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.ll_cart.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FavoriteActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductViewed = ArrayList<String>()
    private var productViewedAdapter: ProductDetailApdater? = null
    private var productViewedList = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference

        // product viewed init
        productViewedAdapter =
            ProductDetailApdater(
                productViewedList,
                this
            )
        // Set rcyclerview horizontal
        rv_product_favorite.layoutManager = LinearLayoutManager(
            application,
            LinearLayoutManager.VERTICAL,
            false
        )
        rv_product_favorite.adapter = productViewedAdapter

        // Get id product to get infor
        val childKey = intent.getStringExtra("childKey")

        // Set name for toolbar
        val nameForToolBar = intent.getStringExtra("nameToolbar")
        tv_List_product_toolbar_name.text = nameForToolBar


        var numViewMore = 0
        numViewMore = intent.getIntExtra("viewMore", 0)
        Log.d("search12346", numViewMore.toString())

        // Choose num view more
        // product viewed or product like to get infor
        if (numViewMore == 0) {
            if (childKey != null) {
                println("key: " + childKey)
                var user: FirebaseUser? = mAuth?.getCurrentUser();
                // Check user loged in firebase yet?
                if (user != null) {
                    getListIdProductViewed(childKey)
                } else {
                    var intent = Intent(this, SignInUpActivity::class.java)
                    startActivity(intent)
                }
            } else {
                ll_favorite_empty.visibility = View.VISIBLE
            }
        }
        // get product sale and viewed by category id
        else if (numViewMore == 1 || numViewMore == 2) {
            val id_category = intent.getStringExtra("id_category")
            if (id_category != null)
                getListProduct(id_category, numViewMore)
        }
        // get product sale of all product
        else if (numViewMore == 3) {
            getListProductSale()
        }

        addEvent()
        getCartCount()
    }

    private fun addEvent() {
        tv_List_product_toolbar_name.setOnClickListener() {
            val intentSearch = Intent(this, MainActivity::class.java)
            intentSearch.putExtra("search", true)
            startActivity(intentSearch)
        }
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

        btn_continue_shopping_favorite.setOnClickListener() {
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
            val uid = user.uid
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

    // get list product best seller or sale
    private fun getListProduct(idCategory: String, numViewMore: Int) {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.PRODUCT)
            .orderByChild(PhysicsConstants.ID_CATEGORY_PRODUCT)
            .equalTo(idCategory)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productViewedList.clear()
                    val current = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("HH")
                    val hours = current.format(dateFormatter).toLong()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.PRODUCT_ID).value as String
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        var price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        if (hours >= 7 && hours < 11)
                            price = price * PhysicsConstants.coefficientMorning
                        else if (hours >= 11 && hours < 13)
                            price = (price * PhysicsConstants.coefficientLunch).toLong()
                        else if (hours >= 13 && hours < 18)
                            price = (price * PhysicsConstants.coefficientAft).toLong()
                        else if (hours >= 18 && hours <= 23)
                            price = (price * PhysicsConstants.coefficientNight).toLong()
                        else if (hours >= 0 && hours < 7)
                            price = (price * PhysicsConstants.coefficientMidNight).toLong()
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long
                        val sold = ds.child(PhysicsConstants.PRODUCT_SOLD).value as Long

                        val product =
                            Product(
                                id,
                                name,
                                price,
                                image,
                                infor,
                                product_count,
                                id_category,
                                sale
                            )
                        if (sale > 1 && numViewMore == 1) {
                            productViewedList.add(product)
                        }
                        if (sold > 10 && numViewMore == 2) {
                            productViewedList.add(product)
                        }
                    }
                    if (!productViewedList.isEmpty()) {
                        productViewedList.reverse()
                        ll_favorite_empty.visibility = View.GONE
                    } else {
                        ll_favorite_empty.visibility = View.VISIBLE
                    }
                    // product
                    productViewedAdapter?.notifyDataSetChanged()
                } else {
                    ll_favorite_empty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                ll_favorite_empty.visibility = View.VISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        query.addValueEventListener(valueEventListener)
    }


    // get list product sale of all product
    private fun getListProductSale() {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.PRODUCT)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productViewedList.clear()
                    val current = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("HH")
                    val hours = current.format(dateFormatter).toLong()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.PRODUCT_ID).value as String
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        var price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        if (hours >= 7 && hours < 11)
                            price = price * PhysicsConstants.coefficientMorning
                        else if (hours >= 11 && hours < 13)
                            price = (price * PhysicsConstants.coefficientLunch).toLong()
                        else if (hours >= 13 && hours < 18)
                            price = (price * PhysicsConstants.coefficientAft).toLong()
                        else if (hours >= 18 && hours <= 23)
                            price = (price * PhysicsConstants.coefficientNight).toLong()
                        else if (hours >= 0 && hours < 7)
                            price = (price * PhysicsConstants.coefficientMidNight).toLong()
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long
                        val sold = ds.child(PhysicsConstants.PRODUCT_SOLD).value as Long

                        val product =
                            Product(
                                id,
                                name,
                                price,
                                image,
                                infor,
                                product_count,
                                id_category,
                                sale
                            )
                        if (sale > 1) {
                            productViewedList.add(product)
                        }
                    }
                    if (!productViewedList.isEmpty()) {
                        ll_favorite_empty.visibility = View.GONE
                        productViewedList.reverse()
                    } else {
                        ll_favorite_empty.visibility = View.VISIBLE
                    }
                    // product
                    productViewedAdapter?.notifyDataSetChanged()
                } else {
                    ll_favorite_empty.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                ll_favorite_empty.visibility = View.VISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    // get list of id product inside user
    // then map id product to product root child
    // show into home fragment if it have data
    private fun getListIdProductViewed(childKey: String) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        val uid = user!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.USERS)
            .child(uid)
            .child(childKey)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrIdProductViewed.clear()
                    for (ds in dataSnapshot.children) {
                        val id: String? =
                            ds.child(PhysicsConstants.VIEWED_PRODUCT_ID).value as String?
                        if (id != null) {
                            arrIdProductViewed.add(id)
                        }
                    }
                    productViewedAdapter?.notifyDataSetChanged()

                    // get product viewed infor
                    if (!arrIdProductViewed.isEmpty()) {
                        ll_favorite_empty.visibility = View.GONE
                        arrIdProductViewed.reverse()
                        getListProductByID(arrIdProductViewed)
                    } else {
                        ll_favorite_empty.visibility = View.VISIBLE
                    }

                } else {
                    ll_favorite_empty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                ll_favorite_empty.visibility = View.VISIBLE
            }
        }
        query.addValueEventListener(valueEventListener)

    }

    // From list product id, then get all infor
    @RequiresApi(Build.VERSION_CODES.O)
    fun getListProductByID(arrId: ArrayList<String>) {
        var product: Product
        productViewedList.clear()
        val current = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("HH")
        val hours = current.format(dateFormatter).toLong()
        for (id in arrId) {
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.PRODUCT)
                .child(id)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        var price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        if (hours >= 7 && hours < 11)
                            price = price * PhysicsConstants.coefficientMorning
                        else if (hours >= 11 && hours < 13)
                            price = (price * PhysicsConstants.coefficientLunch).toLong()
                        else if (hours >= 13 && hours < 18)
                            price = (price * PhysicsConstants.coefficientAft).toLong()
                        else if (hours >= 18 && hours <= 23)
                            price = (price * PhysicsConstants.coefficientNight).toLong()
                        else if (hours >= 0 && hours < 7)
                            price = (price * PhysicsConstants.coefficientMidNight).toLong()
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long

                        product =
                            Product(
                                id,
                                name,
                                price,
                                image,
                                infor,
                                product_count,
                                id_category,
                                sale
                            )

                        productViewedList.add(product)
                        productViewedAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }

    fun onClickQuitFavorite(view: View) {
        finish()
    }
}
