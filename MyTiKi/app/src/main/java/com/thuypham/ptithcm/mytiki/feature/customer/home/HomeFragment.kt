package com.thuypham.ptithcm.mytiki.feature.customer.home

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.base.GridItemDecoration
import com.thuypham.ptithcm.mytiki.base.SlidingImageAdapter
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.data.Status
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.CategoryAdapterHome
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductSaleAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductViewedAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.product.FavoriteActivity
import com.thuypham.ptithcm.mytiki.feature.customer.product.ProductOfCategoryActivity
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.CategoryViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import com.thuypham.ptithcm.mytiki.viewmodel.SlideViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.no_wifi.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Category
    private var categoryList = ArrayList<Category>()
    private lateinit var categoryAdapter: CategoryAdapterHome

    // List
    private var arrAdvertisement = ArrayList<Slide>()

    //product
    private var productAdapter: ProductAdapter? = null
    private var productList = ArrayList<Product>()

    //product sale
    private var productSaleAdapter: ProductSaleAdapter? = null
    private var productSaleList = ArrayList<Product>()

    //Viewed product
    private var productViewedAdapter: ProductViewedAdapter? = null
    private var productViewedList = ArrayList<Product>()

    private val productViewModel: ProductViewModel by viewModel()
    private val categoryViewModel: CategoryViewModel by viewModel()
    private val slideViewModel: SlideViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true


        if (progress == null) return
        if (isConnected) {
            ll_no_wifi.visibility = View.GONE
            inItView()
            slideViewModel.getAllSlide()
            productViewModel.getAllProductSale()
            productViewModel.getListProductViewed()
            productViewModel.getAllProduct()
            categoryViewModel.getAllCategory()
            productViewModel.getCartCount()
            bindViewModel()
        } else {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT)
                .show()
            ll_no_wifi.visibility = View.VISIBLE
        }
        addEvent()
    }

    private fun bindViewModel() {
        categoryViewModel.networkListCategory.observe(viewLifecycleOwner) {
            progress.visibility = if (it.status == Status.RUNNING) View.VISIBLE else View.GONE
        }
        productViewModel.listAllProducts.observe(viewLifecycleOwner) {
            if (it != null) {
                productList.addAll(it)
                productAdapter?.notifyDataSetChanged()
            }
        }
        productViewModel.listAllProductsSale.observe(viewLifecycleOwner) {
            if (it != null) {
                productSaleList.clear()
                it.reverse()
                productSaleList.addAll(it)
                productSaleAdapter?.notifyDataSetChanged()
            }
        }

        categoryViewModel.listCategories.observe(viewLifecycleOwner) {
            if (it != null) {
                categoryList.clear()
                categoryList.addAll(it)
                categoryAdapter.notifyDataSetChanged()
            }
        }

        slideViewModel.listSlide.observe(viewLifecycleOwner) {
            if (it != null) {
                arrAdvertisement.clear()
                arrAdvertisement.addAll(it)
                initSlide()
            }
        }
        productViewModel.cartCount.observe(viewLifecycleOwner) { cartCount ->
            if (cartCount != null) {
                if (cartCount > 0 && tv_number_cart != null) {
                    tv_number_cart.visibility = View.VISIBLE
                    tv_number_cart.text = cartCount.toString()
                } else if (tv_number_cart != null) {
                    tv_number_cart.visibility = View.GONE
                }
            }
        }

        productViewModel.listIdProductViewed.observe(viewLifecycleOwner) { listProductIdViewed ->
            if (listProductIdViewed != null) {
                listProductIdViewed.reverse()
                getListProductByID(listProductIdViewed)
            }
        }
    }

    private fun addEvent() {
        ll_cart_number.setOnClickListener {
            val user: FirebaseUser? = mAuth?.currentUser;
            if (user != null) {
                val intentCart = Intent(context, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(context, AuthActivity::class.java)
                startActivity(intentCart)
            }
        }

        btn_try_connect.setOnClickListener {
            view?.let { it1 -> onViewCreated(it1, null) }
        }

        // view more product sale
        tv_viewmore_product_sale.setOnClickListener {
            val intent = Intent(context, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.saling_product))
            intent.putExtra("viewMore", 3)
            startActivity(intent)
        }

        // view more product viewed
        tv_viewmore_viewed_product.setOnClickListener {
            // intent to FavoriteActivity
            val intentFV = Intent(context, FavoriteActivity::class.java)
            intentFV.putExtra("childKey", Constant.VIEWED_PRODUCT)
            intentFV.putExtra("nameToolbar", getString(R.string.viewd_products))
            intentFV.putExtra("viewMore", 0)
            startActivity(intentFV)
        }
    }


    // get infor for list product by using id of product
    @TargetApi(Build.VERSION_CODES.O)
    fun getListProductByID(arrId: ArrayList<String>) {
        var product: Product?
        productViewedList.clear()
        for (id in arrId) {
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(Constant.PRODUCT)
                .child(id)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        product = ds.getValue(Product::class.java)
                        product?.let { productViewedList.add(it) }
                        ll_viewed_product?.visibility = View.VISIBLE
                        productViewedAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }

    }

    private fun inItView() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        productViewedAdapter = ProductViewedAdapter(productViewedList, requireContext())
        rv_product_viewed.adapter = productViewedAdapter

        // category list
        categoryAdapter = CategoryAdapterHome(categoryList, this)
        rv_category_home.adapter = categoryAdapter

        // product sale init
        productSaleAdapter = ProductSaleAdapter(productSaleList, requireContext())
        rv_product_sale_home.adapter = productSaleAdapter


        // Product list init
        productAdapter = ProductAdapter(productList, requireContext())
        rv_product_home.adapter = productAdapter
        rv_product_home.layoutManager = GridLayoutManager(requireContext(), 2)

        //This will for default android divider
        rv_product_home.addItemDecoration(
            GridItemDecoration(
                10,
                2
            )
        )
    }

    // set image for avt
    private fun initSlide() {
        if (pager == null) return
        pager?.adapter = SlidingImageAdapter(
            requireContext(),
            arrAdvertisement
        ){cateID, cateName->
            val intent = Intent(context, ProductOfCategoryActivity::class.java)
            intent.putExtra("id_category", cateID)
            intent.putExtra("name_category", cateName)
            requireActivity().startActivity(intent)
        }
        indicator?.setViewPager(pager)


        val density = resources.displayMetrics.density

        //Set circle indicator radius
        indicator?.radius = 3 * density
        NUM_PAGES = arrAdvertisement.size

        // Auto start of viewpager
        val handler = Handler()
        val update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            pager?.setCurrentItem(currentPage++, true)
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 3000)

        // Pager listener over indicator
        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(pos: Int) {
            }
        })
    }

    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
    }
}