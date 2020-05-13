package com.thuypham.ptithcm.mytiki.feature.customer.home

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.CategoryAdapterHome
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductSaleAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.ProductViewedAdapter
import com.thuypham.ptithcm.mytiki.feature.customer.product.FavoriteActivity
import com.thuypham.ptithcm.mytiki.feature.customer.product.ProductOfCategoryActivity
import com.thuypham.ptithcm.mytiki.util.Constant
import com.todou.nestrefresh.base.OnRefreshListener
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.ll_cart.*
import kotlinx.android.synthetic.main.loading_layout.*
import kotlinx.android.synthetic.main.no_wifi.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    private var arrIdProductViewed = ArrayList<String>()
    private var productViewedAdapter: ProductViewedAdapter? = null
    private var productViewedList = ArrayList<Product>()

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


        if(progress==null) return
        if (isConnected) {
            ll_no_wifi.visibility = View.GONE
            inItView()
            getDataAVT()
            getListProductSale()
            //   Get list product sviewed
            getListIdProductViewed()
            getDataCategory()
            getListProduct()
            getCartCount()
        } else {
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT)
                .show()
            ll_no_wifi.visibility = View.VISIBLE
        }
        addEvent()
    }

    private fun addEvent() {
        ll_cart_number.setOnClickListener() {
            val user: FirebaseUser? = mAuth?.currentUser;
            if (user != null) {
                val intentCart = Intent(context, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(context, AuthActivity::class.java)
                startActivity(intentCart)
            }
        }

        btn_try_connect.setOnClickListener() {
            view?.let { it1 -> onViewCreated(it1, null) }
        }

        // view more product sale
        tv_viewmore_product_sale.setOnClickListener() {
            val intent = Intent(context, FavoriteActivity::class.java)
            intent.putExtra("nameToolbar", getString(R.string.saling_product))
            intent.putExtra("viewMore", 3)
            startActivity(intent)
        }

        // view more product viewed
        tv_viewmore_viewed_product.setOnClickListener() {
            // intent to FavoriteActivity
            val intentFV = Intent(context, FavoriteActivity::class.java)
            intentFV.putExtra("childKey", Constant.VIEWED_PRODUCT)
            intentFV.putExtra("nameToolbar", getString(R.string.viewd_products))
            intentFV.putExtra("viewMore", 0)
            startActivity(intentFV)
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

    private fun getListProduct() {
        val query = mDatabase!!
            .reference
            .child(Constant.PRODUCT)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productList.clear()
                    val current = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("HH")
                    val hours = current.format(dateFormatter).toLong()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(Constant.PRODUCT_ID).value as String
                        val name = ds.child(Constant.NAME_PRODUCT).value as String
                        var price = ds.child(Constant.PRICE_PRODUCT).value as Long

                        val image = ds.child(Constant.IMAGE_PRODUCT).value as String
                        val infor = ds.child(Constant.INFO_PRODUCT).value as String
                        val product_count = ds.child(Constant.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(Constant.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(Constant.PRODUCT_SALE).value as Long

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
                        productList.add(product)

                    }
                    if (!productList.isEmpty()) {
                        productList.reverse()
                    }
                    // product
                    productAdapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    requireContext(),
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
    private fun getListIdProductViewed() {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        if (user != null) {
            //product viewed
            val uid = user.uid
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(Constant.USER)
                .child(uid)
                .child(Constant.VIEWED_PRODUCT)
                .limitToLast(10)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrIdProductViewed.clear()
                        for (ds in dataSnapshot.children) {
                            val id: String? =
                                ds.child(Constant.VIEWED_PRODUCT_ID).value as String?
                            if (id != null) {
                                arrIdProductViewed.add(id)
                            }
                        }
                        productViewedAdapter?.notifyDataSetChanged()

                        // get product viewed infor
                        if (!arrIdProductViewed.isEmpty()) {
                            arrIdProductViewed.reverse()
                            getListProductByID(arrIdProductViewed)
                        }

                    } else if (ll_viewed_product != null) {
                        arrIdProductViewed.clear()
                        ll_viewed_product.visibility = View.GONE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            query.addValueEventListener(valueEventListener)

        } else ll_viewed_product.visibility = View.GONE
    }

    // get infor for list product by using id of product
    @TargetApi(Build.VERSION_CODES.O)
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
                .child(Constant.PRODUCT)
                .child(id)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        val name = ds.child(Constant.NAME_PRODUCT).value as String
                        var price = ds.child(Constant.PRICE_PRODUCT).value as Long
                        val image = ds.child(Constant.IMAGE_PRODUCT).value as String
                        val infor = ds.child(Constant.INFO_PRODUCT).value as String
                        val product_count = ds.child(Constant.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(Constant.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(Constant.PRODUCT_SALE).value as Long

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
                        if (ll_viewed_product != null) {
                            ll_viewed_product.visibility = View.VISIBLE
                        }
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

        view_refresh_header.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                view_refresh_header.postDelayed({
                    getListProductSale()

                    // Get list product sale
                    getListIdProductViewed()

                    getDataCategory()
                    getListProduct()

                    view_refresh_header.stopRefresh()
                }, 1000)
            }
        })

        // product viewed init
        productViewedAdapter = ProductViewedAdapter(productViewedList, requireContext())
        // Set rcyclerview horizontal
        rv_product_viewed.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rv_product_viewed.adapter = productViewedAdapter

        // category list
        categoryAdapter = CategoryAdapterHome(categoryList, this)
        // Set rcyclerview horizontal
        rv_category_home.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rv_category_home.adapter = categoryAdapter

        // product sale init
        productSaleAdapter = ProductSaleAdapter(productSaleList, requireContext())
        // Set rcyclerview horizontal
        rv_product_sale_home.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
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

    // get list product sale
    private fun getListProductSale() {
        //get 20 product tthat had saled
        val query = mDatabase!!
            .reference.child(Constant.PRODUCT)
            .orderByChild(Constant.PRODUCT_SALE)
            .limitToLast(10)
        val valueEventListener = object : ValueEventListener {
            @TargetApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productSaleList.clear()
                    val current = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("HH")
                    val hours = current.format(dateFormatter).toLong()
                    Log.d("time12112", hours.toString())
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(Constant.PRODUCT_ID).value as String
                        val name = ds.child(Constant.NAME_PRODUCT).value as String
                        var price = ds.child(Constant.PRICE_PRODUCT).value as Long
                        val image = ds.child(Constant.IMAGE_PRODUCT).value as String
                        val infor = ds.child(Constant.INFO_PRODUCT).value as String
                        val product_count = ds.child(Constant.PRODUCT_COUNT).value as Long
                        val id_category =
                            ds.child(Constant.ID_CATEGORY_PRODUCT).value as String
                        val sale = ds.child(Constant.PRODUCT_SALE).value as Long

                        // if sale of product !=0, then save product into productSaleList
                        if (sale != 0L) {
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
                            productSaleList.add(product)
                        }
                    }
                    // product sale change view
                    productSaleList.reverse()
                    productSaleAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    // set image for avt
    fun inIt() {
        if(pager==null) return
        pager?.adapter = SlidingImageAdapter(
            requireContext(),
            arrAdvertisement
        )
        indicator?.setViewPager(pager)


        val density = resources.displayMetrics.density

        //Set circle indicator radius
        indicator?.radius = 5 * density
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
        indicator?.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                currentPage = position

            }

            override fun onPageScrolled(pos: Int, arg1: Float, arg2: Int) {

            }

            override fun onPageScrollStateChanged(pos: Int) {

            }
        })
        indicator?.setOnClickListener() {
            val intent = Intent(context, ProductOfCategoryActivity::class.java)
            intent.putExtra("id_category", arrAdvertisement[currentPage].id_category)
            intent.putExtra("name_category", arrAdvertisement[currentPage].name_category)
            requireActivity().startActivity(intent)
        }
    }

    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
    }

    // get all avt
    private fun getDataAVT() {
        if(progress==null) return
        mDatabaseReference = mDatabase!!.reference.child(Constant.SLIDE)
        progress.visibility = View.VISIBLE
        ll_home.visibility = View.GONE
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrAdvertisement.clear()
                    for (ds in dataSnapshot.children) {
                        val id = ds.child(Constant.CATEGORY_ID).value as String
                        val name = ds.child(Constant.SLIDE_NAME).value as String
                        val image = ds.child(Constant.SLIDE_IMAGE).value as String
                        val id_category = ds.child(Constant.SLIDE_ID_CATEGORY).value as String
                        val name_category =
                            ds.child(Constant.SLIDE_NAME_CATEGORY).value as String
                        val advertisement =
                            Slide(
                                name,
                                id,
                                image,
                                id_category,
                                name_category
                            )
                        arrAdvertisement.add(advertisement)
                    }
                    inIt()

                    if(progress==null) return
                    progress.visibility = View.GONE
                    ll_home.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    getString(com.thuypham.ptithcm.mytiki.R.string.error_load_category),
                    Toast.LENGTH_LONG
                ).show()
                progress.visibility = View.GONE
                ll_home.visibility = View.VISIBLE
            }

        }
        mDatabaseReference.addValueEventListener(valueEventListener)
    }

    // get data for category
    private fun getDataCategory() {
        mDatabaseReference = mDatabase!!
            .reference
            .child(Constant.CATEGORY)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    categoryList.clear()
                    var category:Category?
                    for (ds in dataSnapshot.children) {
                        category = ds.getValue(Category::class.java)
                        if (category != null) {
                            categoryList.add(category)
                        }

                    }
                    categoryAdapter.notifyDataSetChanged()
                }

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
}