package com.thuypham.ptithcm.mytiki.main.cart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.product.adapter.ListProductAdapter
import com.thuypham.ptithcm.mytiki.main.product.model.Product

class CartActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    //Viewed product
    private var arrIdProductCart = ArrayList<String>()
    private var productCartAdapter: ListProductAdapter? = null
    private var productCartList = ArrayList<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
    }

    fun onClickQuiteCart(view: View) {finish()}
}
