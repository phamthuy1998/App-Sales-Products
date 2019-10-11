package com.thuypham.ptithcm.mytiki.main.product.activity

import android.app.Dialog
import android.content.Intent
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
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity.CartActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import com.thuypham.ptithcm.mytiki.main.product.adapter.ProductDetailApdater
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.fragment_continue_shopping.*
import kotlinx.android.synthetic.main.ll_cart.*

class FavoriteActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductViewed = ArrayList<String>()
    private var productViewedAdapter: ProductDetailApdater? = null
    private var productViewedList = ArrayList<Product>()

    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

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
        val id_category = intent.getStringExtra("id_category")
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

        btn_continue_shopping_favorite.setOnClickListener(){
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

    // get list product best seller or sale
    private fun getListProduct(idCategory: String, numViewMore: Int) {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.PRODUCT)
            .orderByChild(PhysicsConstants.ID_CATEGORY_PRODUCT)
            .equalTo(idCategory)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productViewedList.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.PRODUCT_ID).value as String
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        val price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long
                        val sold = ds.child(PhysicsConstants.PRODUCT_SOLD).value as Long

                        val product =
                            Product(id, name, price, image, infor, product_count, id_category, sale)
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
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
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
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productViewedList.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.PRODUCT_ID).value as String
                        val name = ds.child(PhysicsConstants.NAME_PRODUCT).value as String
                        val price = ds.child(PhysicsConstants.PRICE_PRODUCT).value as Long
                        val image = ds.child(PhysicsConstants.IMAGE_PRODUCT).value as String
                        val infor = ds.child(PhysicsConstants.INFOR_PRODUCT).value as String
                        val product_count = ds.child(PhysicsConstants.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(PhysicsConstants.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(PhysicsConstants.PRODUCT_SALE).value as Long
                        val sold = ds.child(PhysicsConstants.PRODUCT_SOLD).value as Long

                        val product =
                            Product(id, name, price, image, infor, product_count, id_category, sale)
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
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
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
        println("user id: $uid")
        mDatabase = FirebaseDatabase.getInstance()
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.USERS)
            .child(uid)
            .child(childKey)
            .limitToLast(20)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrIdProductViewed.clear()
                    println("viewed favorite product co du lieu")
                    for (ds in dataSnapshot.children) {
                        val id: String? =
                            ds.child(PhysicsConstants.VIEWED_PRODUCT_ID).value as String?
                        println(" id product: $id")
                        if (id != null) {
                            arrIdProductViewed.add(id)
                        }
                    }
                    productViewedAdapter?.notifyDataSetChanged()

                    // get product viewed infor
                    if (!arrIdProductViewed.isEmpty()) {
                        ll_favorite_empty.visibility = View.GONE
                        println("list khong  favorite rong")
                        getListProductByID(arrIdProductViewed)
                    } else {
                        ll_favorite_empty.visibility = View.VISIBLE
                    }

                } else {
                    ll_favorite_empty.visibility = View.VISIBLE
                    println("k co dl favorite viewed")
                    //  showDialog()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                ll_favorite_empty.visibility = View.VISIBLE
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        query.addValueEventListener(valueEventListener)

    }

    // From list product id, then get all infor
    fun getListProductByID(arrId: ArrayList<String>) {
        var product: Product
        productViewedList.clear()
        for (id in arrId) {
            println("vo toi day luon nhi")
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.PRODUCT)
                .child(id)

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
                            Product(id, name, price, image, infor, product_count, id_category, sale)

                        productViewedList.add(product)

                        println("lay sp thanh cong")
                        println("size mang xem1: " + productViewedList.size)
//                        showDialog()
                        productViewedAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("lay 1 sp k thanh cong")
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }


    fun onClickQuitFavorite(view: View) {
        finish()
    }

    fun searchProduct(view: View) {}
    fun onClickCart(view: View) {}
}
