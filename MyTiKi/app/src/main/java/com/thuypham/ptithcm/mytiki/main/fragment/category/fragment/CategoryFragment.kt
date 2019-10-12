package com.thuypham.ptithcm.mytiki.main.fragment.category.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.category.adapter.CategoryAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import kotlinx.android.synthetic.main.category_fragment.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.activity.CartActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.no_wifi.*


class CategoryFragment : Fragment() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Category
    private var adapter: CategoryAdapter? = null
    private var categoryList = ArrayList<Category>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inIt()


        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            ll_no_wifi_category.visibility = View.GONE
            getDataCategory()
        } else {
            Toast.makeText(requireContext(),R.string.no_internet_connection,Toast.LENGTH_SHORT  ).show()
            ll_no_wifi_category.visibility = View.VISIBLE
            Log.d("abc", "khong co ket noi internet")
        }

//        for(i in 0..20){
//        mDatabaseReference = mDatabase!!.reference
//        val currentUserDb = mDatabaseReference!!.child(PhysicsConstants.PRODUCT).push()
//        currentUserDb.child(PhysicsConstants.PRODUCT_ID).setValue(currentUserDb.key)
//        currentUserDb.child(PhysicsConstants.NAME_PRODUCT).setValue("ao")
//        currentUserDb.child(PhysicsConstants.PRICE_PRODUCT).setValue(99000)
//        currentUserDb.child(PhysicsConstants.IMAGE_PRODUCT).setValue("abc")
//        currentUserDb.child(PhysicsConstants.INFOR_PRODUCT).setValue("abc")
//        currentUserDb.child(PhysicsConstants.PRODUCT_COUNT).setValue(10)
//        currentUserDb.child(PhysicsConstants.ID_CATEGORY_PRODUCT).setValue("-LmwzhwCLb3CL6u9atPw")
//        currentUserDb.child(PhysicsConstants.PRODUCT_SALE).setValue(0)
//        currentUserDb.child(PhysicsConstants.PRODUCT_SOLD).setValue(10)}
        addEvent()
        getCartCount()
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

    private fun addEvent() {
        btn_try_connect.setOnClickListener(){
            view?.let { it1 -> onViewCreated(it1,null) }
        }

        ll_cart_number.setOnClickListener() {
            val user: FirebaseUser? = mAuth?.getCurrentUser();
            if (user != null) {
                val intentCart = Intent(context, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(context, SignInUpActivity::class.java)
                startActivity(intentCart)
            }
        }

        gv_category.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Get the GridView selected/clicked item text
                val selectedItem = parent.getItemAtPosition(position).toString()

                // Display the selected/clicked item text and position on TextView
                Toast.makeText(
                    requireContext(),
                    "GridView item clicked : ${categoryList[position].nameCategory} \\nAt index position : $position\"",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getDataCategory() {
        progress.visibility = View.VISIBLE
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                categoryList.clear()
                for (ds in dataSnapshot.children) {
                    val id = ds.child(PhysicsConstants.CATEGORY_ID).value as String
                    val name = ds.child(PhysicsConstants.CATEGORY_NAME).value as String
                    val image = ds.child(PhysicsConstants.CATEGORY_IMAGE).value as String
                    val count = ds.child(PhysicsConstants.CATEGORY_COUNT).value as Long
                    println("lay du lieu ten $name")
                    println("lay du lieu  anh$image")
                    println("lay du lieu so $count")
                    println("lay du lieu id $id")

                    val category = Category(id, name, image, count)
                    categoryList.add(category)

                }
                adapter?.notifyDataSetChanged()

                progress.visibility = View.GONE

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference.addValueEventListener(valueEventListener)
    }


    private fun inIt() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child(PhysicsConstants.CATEGORY_table)
        // create adapter
        adapter = CategoryAdapter(requireContext(), categoryList)
        gv_category.adapter = adapter
    }
}