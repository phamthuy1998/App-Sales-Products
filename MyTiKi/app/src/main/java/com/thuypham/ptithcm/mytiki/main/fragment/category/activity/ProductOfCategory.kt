package com.thuypham.ptithcm.mytiki.main.fragment.category.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Advertisement
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductSaleAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductViewedAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.support.GridItemDecoration
import com.thuypham.ptithcm.mytiki.main.product.activity.FavoriteActivity
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import com.thuypham.ptithcm.mytiki.viewsHelp.SlidingImage_Adapter
import kotlinx.android.synthetic.main.activity_product_of_category.*
import kotlinx.android.synthetic.main.loading_layout.*
import java.util.*
import kotlin.collections.ArrayList

class ProductOfCategory : AppCompatActivity() {


    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // List
    private var arrAdvertisement = ArrayList<Advertisement>()

    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
    }

    //product
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<Product>()

    //product sale
    private var productSaleAdapter: ProductSaleAdapter? = null
    private var productSaleList = ArrayList<Product>()

    private var productBestSellerAdapter: ProductViewedAdapter? = null
    private var productBestSellerList = ArrayList<Product>()

    private var id_category =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_of_category)

        getIdCategory()
        addEvent()
    }

    private fun addEvent() {
        // show more sale product
        tv_viewmore_product_sale_category.setOnClickListener() {
            var intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.saling_product))
            intent.putExtra("id_category", id_category)
            intent.putExtra("viewMore", 1)
            startActivity(intent)
        }

        //show more best seller product
        tv_viewmore_best_slae.setOnClickListener(){
            var intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.saling_product))
            intent.putExtra("id_category", id_category)
            intent.putExtra("viewMore", 2)
            startActivity(intent)
        }

    }

    private fun getIdCategory() {
        // Get id category to get list item
        id_category = intent.getStringExtra("id_category")
        if (id_category != null) {
            val name_category = intent.getStringExtra("name_category")
            tv_toolbar_product_category.text = name_category

            // init view
            initView()

            // get list AVT
            getDataAVT(id_category)

            // get all list product
            getListProduct(id_category)

        }
    }

    private fun getListProduct(idCategory: String) {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.PRODUCT)
            .orderByChild(PhysicsConstants.ID_CATEGORY_PRODUCT)
            .equalTo(idCategory)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productList.clear()

                    var i = 0
                    var j = 0
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
                        if (sale > 0 && i < 5) {
                            i++// litmited the count of product
                            productSaleList.add(product)
                        }
                        if (sold > 10 && j < 5) {
                            j++
                            productBestSellerList.add(product)
                        }
                        productList.add(product)
                        println("ten san pham: ${name}")

                    }
                    println("so phan tu cua product: ${productList.size}")
                    if (productSaleList.isEmpty() && productBestSellerList.isEmpty() && productList.isEmpty())
                        tv_list_empty.visibility = View.VISIBLE
                    else tv_list_empty.visibility = View.GONE

                    // product
                    productAdapter?.notifyDataSetChanged()
                    productBestSellerAdapter?.notifyDataSetChanged()
                    productSaleAdapter?.notifyDataSetChanged()
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

    private fun initView() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()


        // product best seller init
        productBestSellerAdapter = ProductViewedAdapter(productBestSellerList, applicationContext)
        // Set rcyclerview horizontal
        rv_product_best_sale_category.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rv_product_best_sale_category.adapter = productBestSellerAdapter

        // product sale init
        productSaleAdapter = ProductSaleAdapter(productSaleList, applicationContext)
        // Set rcyclerview horizontal
        rv_product_sale_category.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rv_product_sale_category.adapter = productSaleAdapter

        // Product list init
        productAdapter = ProductAdapter(productList, this)
        rv_all_product_categgory.adapter = productAdapter
        rv_all_product_categgory.layoutManager = GridLayoutManager(this, 2)
        //This will for default android divider
        rv_all_product_categgory.addItemDecoration(GridItemDecoration(10, 2))
    }

    private fun getDataAVT(idCategory: String) {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.ADVERTIEMENT)
            .orderByChild(PhysicsConstants.AVT_ID_CATEGORY)
            .equalTo(idCategory)

        println("max category" + idCategory)

        // Show progressbar
        progress.visibility = View.VISIBLE

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrAdvertisement.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(PhysicsConstants.CATEGORY_ID).value as String
                        val name = ds.child(PhysicsConstants.NAME_AVT).value as String
                        val image = ds.child(PhysicsConstants.IMAGE_AVT).value as String
                        val id_category = ds.child(PhysicsConstants.AVT_ID_CATEGORY).value as String
                        val name_category =
                            ds.child(PhysicsConstants.AVT_NAME_CATEGORY).value as String

                        val advertisement =
                            Advertisement(name, id, image, id_category, name_category)
                        arrAdvertisement.add(advertisement)
                    }
                    println("mang quang cao size: " + arrAdvertisement.size)
                    inIt()
                    progress.visibility = View.GONE
                } else {
                    println("mang quang cao rong")
                    progress.visibility = View.GONE
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
                progress.visibility = View.GONE
            }

        }
        query.addValueEventListener(valueEventListener)
    }


    fun inIt() {
        pager_category!!.adapter = SlidingImage_Adapter(applicationContext, arrAdvertisement)
        indicator_category.setViewPager(pager_category)
        val density = resources.displayMetrics.density

        //Set circle indicator radius
        indicator_category.setRadius(5 * density)
        println("so trang ne: ${arrAdvertisement.size}")
        NUM_PAGES = arrAdvertisement.size

        // Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            pager_category?.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)

        // Pager listener over indicator
        indicator_category.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                currentPage = position

            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(pos: Int) {

            }
        })
    }

    fun onClickQuiteCategory(view: View) {
        finish()
    }


}
