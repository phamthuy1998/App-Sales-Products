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
import com.thuypham.ptithcm.mytiki.main.product.adapter.ListProductAdapter
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.fragment_continue_shopping.*

class FavoriteActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductViewed = ArrayList<String>()
    private var productViewedAdapter: ListProductAdapter? = null
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
            ListProductAdapter(
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
                getListIdProductViewed(childKey)
            } else {
                //showDialog()
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
                        println("ten san pham: ${name}")

                    }
                    // product
                    productViewedAdapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
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

                        println("ten san pham: ${name}")

                    }
                    // product
                    productViewedAdapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
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


    //  show dialog to warning that user not Verified email when they create acc
    private fun showDialog() {
//                Theme_Black_NoTitleBar_Fullscreen
        val dialog = Dialog(applicationContext, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setCancelable(false)
        dialog.toString()
        dialog.setContentView(com.thuypham.ptithcm.mytiki.R.layout.fragment_continue_shopping)

        dialog.btn_continue_shopping.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }
        // dialog.btn_cancel_active_acc.setOnClickListener { dialog.dismiss() }

        dialog.show()

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
                        println("list khong  favorite rong")
                        getListProductByID(arrIdProductViewed)
                    }

                } else {
                    println("k co dl favorite viewed")
                    //  showDialog()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
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
