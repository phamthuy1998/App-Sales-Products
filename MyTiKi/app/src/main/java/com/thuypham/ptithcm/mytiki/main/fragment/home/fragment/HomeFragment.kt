package com.thuypham.ptithcm.mytiki.main.fragment.home.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.fragment.category.activity.ProductOfCategory
import com.thuypham.ptithcm.mytiki.main.fragment.category.fragment.CategoryFragment
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Advertisement
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.CategoryAdapterHome
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductSaleAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.ProductViewedAdapter
import com.thuypham.ptithcm.mytiki.main.fragment.home.support.GridItemDecoration
import com.thuypham.ptithcm.mytiki.main.product.activity.FavoriteActivity
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import com.thuypham.ptithcm.mytiki.viewsHelp.SlidingImage_Adapter
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.loading_layout.*
import java.util.*
import kotlin.collections.ArrayList
import com.todou.nestrefresh.base.OnRefreshListener
import kotlinx.android.synthetic.main.activity_main.*


class HomeFragment : Fragment() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    // Category
    private var categoryList = ArrayList<Category>()
    private lateinit var categoryAdapter: CategoryAdapterHome

    // List
    private var arrAdvertisement = ArrayList<Advertisement>()

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
        Log.d(tag, "\nonCreateView");
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(tag, "\non onViewCreated")
        println("save install null")
        inItView()
        getDataAVT()
        getListProductSale()

        //   Get list product sviewed
        getListIdProductViewed()

        println("12hg " + arrIdProductViewed.size)

        getDataCategory()
        getListProduct()
        addEvent()
    }

    private fun addEvent() {

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
            intentFV.putExtra("childKey", PhysicsConstants.VIEWED_PRODUCT)
            intentFV.putExtra("nameToolbar", getString(R.string.viewd_products))
            intentFV.putExtra("viewMore", 0)
            startActivity(intentFV)
        }

    }

    private fun getListProduct() {
        val query = mDatabase!!
            .reference
            .child(PhysicsConstants.PRODUCT)
            .limitToLast(30)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productList.clear()
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

                        val product =
                            Product(id, name, price, image, infor, product_count, id_category, sale)
                        productList.add(product)
                        println("ten san pham: ${name}")

                    }
                    println("so phan tu cua product: ${productList.size}")
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
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
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
            println("user id: $uid")
            mDatabase = FirebaseDatabase.getInstance()
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.USERS)
                .child(uid)
                .child(PhysicsConstants.VIEWED_PRODUCT)
                .limitToLast(20)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        arrIdProductViewed.clear()
                        println("viewed product co du lieu")
                        for (ds in dataSnapshot.children) {
                            val id: String? =
                                ds.child(PhysicsConstants.VIEWED_PRODUCT_ID).value as String?
                            println(" id product: $id")
                            if (id != null) {
                                arrIdProductViewed.add(id)
                            }
                        }
                        productViewedAdapter?.notifyDataSetChanged()
                        println("so phan tu id product: ${arrIdProductViewed.size}")

                        // get product viewed infor
                        if (!arrIdProductViewed.isEmpty()) {
                            println("list khong rong")
                            getListProductByID(arrIdProductViewed)
                        }

                    } else {
                        println("k co dl viewed")
                        ll_viewed_product.visibility = View.GONE
                    }
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
            query.addValueEventListener(valueEventListener)

        } else ll_viewed_product.visibility = View.GONE
    }

    // get infor for list product by using id of product
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
                        ll_viewed_product.visibility = View.VISIBLE
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
        rv_product_home.addItemDecoration(GridItemDecoration(10, 2))
    }

    // get list product sale
    private fun getListProductSale() {
        //get 20 product tthat had saled
        val query = mDatabase!!
            .reference.child(PhysicsConstants.PRODUCT)
            .orderByChild(PhysicsConstants.PRODUCT_SALE)
            .limitToLast(20)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    productSaleList.clear()
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

                        // if sale of product !=0, then save product into productSaleList
                        if (sale != 0L) {
                            val product = Product(
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
                            println("ten san pham: ${name}")
                        }
                    }
                    println("so phan tu cua sale product: ${productSaleList.size}")
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
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    // set image for avt
    fun inIt() {
        pager!!.adapter = SlidingImage_Adapter(requireContext(), arrAdvertisement)
        indicator.setViewPager(pager)


        val density = resources.displayMetrics.density

        //Set circle indicator radius
        indicator.setRadius(5 * density)
        NUM_PAGES = arrAdvertisement.size

        // Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            pager?.setCurrentItem(currentPage++, true)
        }

        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
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
        indicator.setOnClickListener() {
            println("trang hien tai: " + arrAdvertisement[currentPage].id_category)
            println("tên: " + arrAdvertisement[currentPage].name)
            var intent = Intent(context, ProductOfCategory::class.java)
            intent.putExtra("id_category", arrAdvertisement[currentPage].id_category)
            intent.putExtra("name_category", arrAdvertisement[currentPage].name_category)
            context!!.startActivity(intent)
        }
    }

    companion object {
        private var currentPage = 0
        private var NUM_PAGES = 0
    }

    // get all avt
    private fun getDataAVT() {
        mDatabaseReference = mDatabase!!.reference.child(PhysicsConstants.ADVERTIEMENT)
        progress.visibility = View.VISIBLE
        ll_home.visibility = View.GONE
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
                        println("lay du lieu ten $name")
                        println("lay du lieu  anh$image")
                        println("lay du lieu id cate $id_category")
                        println("lay du lieu id $id")

                        val advertisement =
                            Advertisement(name, id, image, id_category, name_category)
                        arrAdvertisement.add(advertisement)
                    }
                    println("si mang ne: ${arrAdvertisement.size}")
                    inIt()
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
                Log.w("LogFragment", "loadLog:onCancelled", databaseError.toException())
                progress.visibility = View.GONE
                ll_home.visibility = View.VISIBLE
            }

        }
        mDatabaseReference.addValueEventListener(valueEventListener)
    }

    // get data for category
    private fun getDataCategory() {
        mDatabaseReference = mDatabase!!
            .reference!!
            .child(PhysicsConstants.CATEGORY_table)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
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
                    categoryAdapter?.notifyDataSetChanged()
                }

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

}