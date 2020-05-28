package com.thuypham.ptithcm.mytiki.feature.customer.product

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.feature.customer.product.adapter.ProductDetailAdapter
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.ll_cart.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    //Viewed product
    private var arrIdProductViewed = ArrayList<String>()
    private val productViewedAdapter: ProductDetailAdapter by lazy { ProductDetailAdapter() }
    private var productViewedList = ArrayList<Product>()


    private val productViewModel: ProductViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference

        initViews()

        // Get id product to get info
        val childKey = intent.getStringExtra("childKey")

        // Set name for toolbar
        val nameForToolBar = intent.getStringExtra("nameToolbar")
        tv_List_product_toolbar_name.text = nameForToolBar


        var numViewMore = 0
        numViewMore = intent.getIntExtra("viewMore", 0)

        // Choose num view more
        // product viewed or product like to get infor
        if (numViewMore == 0) {
            getListIdProductViewed(childKey)
        }
        // get product sale and viewed by category id
        else if (numViewMore == 1) {
            val idCategory = intent.getStringExtra("id_category")
            if (idCategory != null)
                productViewModel.getAllProductSaleOfCate(idCategory, 1)
        } else if (numViewMore == 2) {
            val idCategory = intent.getStringExtra("id_category")
            if (idCategory != null)
                productViewModel.getAllProductSoldOfCate(idCategory, 1)
        }

        // get product sale of all product
        else if (numViewMore == 3) {
            productViewModel.getAllProductSale(1)
        }

        addEvent()
        productViewModel.getCartCount()
        bindViewModel()
    }

    private fun initViews() {
        rv_product_favorite.adapter = productViewedAdapter
    }

    private fun bindViewModel() {

        productViewModel.cartCount.observe(this) { cartCount ->
            if (cartCount != null) {
                if (cartCount > 0 && tv_number_cart != null) {
                    tv_number_cart.visibility = View.VISIBLE
                    tv_number_cart.text = cartCount.toString()
                } else if (tv_number_cart != null) {
                    tv_number_cart.visibility = View.GONE
                }
            }
        }

        productViewModel.listAllProductsSale.observe(this) {
            if (it.isNotEmpty()) {
                ll_favorite_empty.visibility = View.GONE
                it.reverse()
                productViewedAdapter.setData(it)
            } else {
                ll_favorite_empty.visibility = View.VISIBLE
            }
            productViewedAdapter.notifyDataSetChanged()
        }

        productViewModel.listAllProductsSaleOfCate.observe(this) {
            if (it.isNotEmpty()) {
                ll_favorite_empty.visibility = View.GONE
                productViewedAdapter.setData(it)
            } else {
                ll_favorite_empty.visibility = View.VISIBLE
            }
            productViewedAdapter.notifyDataSetChanged()
        }

        productViewModel.listAllProductsSoldOfCate.observe(this) {
            if (it.isNotEmpty()) {
                ll_favorite_empty.visibility = View.GONE
                productViewedAdapter.setData(it)
            } else {
                ll_favorite_empty.visibility = View.VISIBLE
            }
            productViewedAdapter.notifyDataSetChanged()
        }
    }

    private fun addEvent() {
        tv_List_product_toolbar_name.setOnClickListener {
            val intentSearch = Intent(this, MainActivity::class.java)
            intentSearch.putExtra("search", true)
            startActivity(intentSearch)
        }
        ll_cart_number.setOnClickListener { startActivity<CartActivity>() }
        btn_continue_shopping_favorite.setOnClickListener { startActivity<MainActivity>() }
    }

    // get list of id product inside user
    // then map id product to product root child
    private fun getListIdProductViewed(childKey: String) {
        val user: FirebaseUser? = mAuth?.currentUser
        val uid = user?.uid
        mDatabase = FirebaseDatabase.getInstance()
        val query = mDatabase!!.reference.child(Constant.USER).child(uid.toString()).child(childKey)
        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrIdProductViewed.clear()
                    for (ds in dataSnapshot.children) {
                        val id: String? = ds.child(Constant.VIEWED_PRODUCT_ID).value as String?
                        id?.let { arrIdProductViewed.add(it) }
                    }
                    if (arrIdProductViewed.isNotEmpty()) {
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

    // From list product id, then get all info of product
    fun getListProductByID(arrId: ArrayList<String>) {
        var product: Product?
        for (id in arrId) {
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase?.reference?.child(Constant.PRODUCT)?.child(id)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        product = ds.getValue(Product::class.java)
                        if (product?.del == false)
                            product?.let { productViewedAdapter.addProduct(it) }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query?.addValueEventListener(valueEventListener)
        }
    }

    fun onClickQuitFavorite(view: View) {
        finish()
    }
}
