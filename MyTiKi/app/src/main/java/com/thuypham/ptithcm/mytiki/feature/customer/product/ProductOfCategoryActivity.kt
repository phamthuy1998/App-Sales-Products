package com.thuypham.ptithcm.mytiki.feature.customer.product

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.GridItemDecoration
import com.thuypham.ptithcm.mytiki.base.SlidingImageAdapter
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductSaleAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductViewedAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.main.MainActivity
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.SlideViewModel
import kotlinx.android.synthetic.main.activity_product_of_category.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.loading_layout.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class ProductOfCategoryActivity : AppCompatActivity() {


    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // List
    private var arrAdvertisement = ArrayList<Slide>()

    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
    }

    private val productViewModel: ProductViewModel by viewModel()
    private val slideViewModel: SlideViewModel by viewModel()

    //product
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<Product>()

    //product sale
    private var productSaleAdapter: ProductSaleAdapter? = null
    private var productSaleList = ArrayList<Product>()

    private var productBestSellerAdapter: ProductViewedAdapter? = null
    private var productBestSellerList = ArrayList<Product>()

    private var idCategory: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_of_category)
        getIdCategory()
        addEvent()
        productViewModel.getCartCount()
        bindViewModel()
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

        productViewModel.listProduct.observe(this) { listProduct ->
            if (listProduct.isNotEmpty()) {
                productList.clear()
                productList.addAll(listProduct)
                productAdapter?.notifyDataSetChanged()
                llProductEmpty.gone()
            } else llProductEmpty.visible()
        }
        productViewModel.networkListProduct.observe(this) {
            when (it.status) {
                Status.RUNNING -> {
                    progress.visible()
                }
                Status.SUCCESS -> {
                    progress.gone()
                    llProductEmpty.gone()
                }
                Status.LOADING_PROCESS -> {
                }
                Status.FAILED -> {
                    progress.gone()
                    llProductEmpty.visible()
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }


        productViewModel.networkAllProductsSoldOfCate.observe(this) {
            when (it.status) {
                Status.RUNNING -> {
                    progress.visible()
                }
                Status.SUCCESS -> {
                    progress.gone()
                    tv_best_sale.visible()
                    tv_viewmore_best_slae.visible()
                }
                Status.LOADING_PROCESS -> {
                }
                Status.FAILED -> {
                    progress.gone()
                    tv_best_sale.gone()
                    tv_viewmore_best_slae.gone()
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }


        productViewModel.listAllProductsSoldOfCate.observe(this) { listProductSold ->
            if (listProductSold.isNotEmpty()) {
                productBestSellerList.clear()
                productBestSellerList.addAll(listProductSold)
                productBestSellerAdapter?.notifyDataSetChanged()
            }
        }

        productViewModel.networkAllProductsSaleOfCate.observe(this) {
            when (it.status) {
                Status.RUNNING -> {
                    progress.visible()
                }
                Status.SUCCESS -> {
                    progress.gone()
                    tv_salse.visible()
                    tv_viewmore_product_sale_category.visible()
                }
                Status.LOADING_PROCESS -> {
                }
                Status.FAILED -> {
                    progress.gone()
                    tv_salse.gone()
                    tv_viewmore_product_sale_category.gone()
                    Toast.makeText(this, it.msg, Toast.LENGTH_LONG).show()
                }
            }
        }

        productViewModel.listAllProductsSaleOfCate.observe(this) { listProductSale ->
            if (listProductSale.isNotEmpty()) {
                productSaleList.clear()
                productSaleList.addAll(listProductSale)
                productSaleAdapter?.notifyDataSetChanged()
            }
        }

        slideViewModel.listSlide.observe(this) {
            if (it != null) {
                arrAdvertisement.addAll(it)
                initSlide()
            }
        }
    }

    private fun addEvent() {

        btnContinueShoppingProduct.setOnClickListener {  startActivity<MainActivity>() ; finish() }
        ll_cart_number.setOnClickListener { startActivity<CartActivity>() }

        // show more sale product
        tv_viewmore_product_sale_category.setOnClickListener() {
            val intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.saling_product))
            intent.putExtra("id_category", idCategory)
            intent.putExtra("viewMore", 1)
            startActivity(intent)
        }

        //show more best seller product
        tv_viewmore_best_slae.setOnClickListener() {
            val intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.best_seller))
            intent.putExtra("id_category", idCategory)
            intent.putExtra("viewMore", 2)
            startActivity(intent)
        }

    }

    private fun getIdCategory() {
        // Get id category to get list item
        idCategory = intent.getStringExtra("id_category")
        if (idCategory == null) return
        val nameCategory = intent.getStringExtra("name_category")
        tv_toolbar_product_category.text = nameCategory

        // init view
        initView()
        slideViewModel.getAllSlideOfCategory(idCategory!!)
        // get all list product
        productViewModel.getAllProductOfCategory(idCategory!!)
        productViewModel.getAllProductSoldOfCate(idCategory!!)
        productViewModel.getAllProductSaleOfCate(idCategory!!)

    }

    private fun initView() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        // product best seller init
        productBestSellerAdapter = ProductViewedAdapter(productBestSellerList, applicationContext)
        // Set recyclerview horizontal
        rv_product_best_sale_category.adapter = productBestSellerAdapter

        // product sale init
        productSaleAdapter = ProductSaleAdapter(productSaleList, applicationContext)
        // Set recyclerview horizontal
        rv_product_sale_category.adapter = productSaleAdapter

        // Product list init
        productAdapter = ProductAdapter(productList, this)
        rv_all_product_categgory.adapter = productAdapter
        rv_all_product_categgory.layoutManager = GridLayoutManager(this, 2)
        rv_all_product_categgory.addItemDecoration(GridItemDecoration(10, 2))
    }


    private fun initSlide() {
        if (arrAdvertisement.isEmpty())
            rlSlide.gone()
        else rlSlide.visible()

        pager_category?.adapter = SlidingImageAdapter(applicationContext, arrAdvertisement, null)
        indicator_category.setViewPager(pager_category)
        val density = resources.displayMetrics.density

        //Set circle indicator radius
        indicator_category.radius = 5 * density
        NUM_PAGES = arrAdvertisement.size

        // Auto start of viewpager
        val handler = Handler()
        val update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            pager_category?.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
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
