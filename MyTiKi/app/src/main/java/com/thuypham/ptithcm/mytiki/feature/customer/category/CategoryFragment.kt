package com.thuypham.ptithcm.mytiki.feature.customer.category

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
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
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.feature.authentication.SignInUpActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.category.adapter.CategoryAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import kotlinx.android.synthetic.main.category_fragment.*
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
        }

//        for(i in 0..20){
//        mDatabaseReference = mDatabase!!.reference
//        val currentUserDb = mDatabaseReference!!.child(Constant.PRODUCT).push()
//        currentUserDb.child(Constant.PRODUCT_ID).setValue(currentUserDb.key)
//        currentUserDb.child(Constant.NAME_PRODUCT).setValue("ao")
//        currentUserDb.child(Constant.PRICE_PRODUCT).setValue(99000)
//        currentUserDb.child(Constant.IMAGE_PRODUCT).setValue("abc")
//        currentUserDb.child(Constant.INFOR_PRODUCT).setValue("abc")
//        currentUserDb.child(Constant.PRODUCT_COUNT).setValue(10)
//        currentUserDb.child(Constant.ID_CATEGORY_PRODUCT).setValue("-LmwzhwCLb3CL6u9atPw")
//        currentUserDb.child(Constant.PRODUCT_SALE).setValue(0)
//        currentUserDb.child(Constant.PRODUCT_SOLD).setValue(10)}
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
                    "GridView item clicked : ${categoryList[position].name} \\nAt index position : $position\"",
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
                var category:Category?
                for (ds in dataSnapshot.children) {
                    category = ds.getValue(Category::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }

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
            }
        }
        mDatabaseReference.addValueEventListener(valueEventListener)
    }


    private fun inIt() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child(Constant.CATEGORY)
        // create adapter
        adapter = CategoryAdapter(requireContext(), categoryList)
        gv_category.adapter = adapter
    }
}