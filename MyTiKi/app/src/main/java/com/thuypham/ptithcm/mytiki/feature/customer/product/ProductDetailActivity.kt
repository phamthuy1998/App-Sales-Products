package com.thuypham.ptithcm.mytiki.feature.customer.product

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.bumptech.glide.Glide
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.ext.gone
import com.thuypham.ptithcm.mytiki.ext.visible
import com.thuypham.ptithcm.mytiki.feature.authentication.AuthActivity
import com.thuypham.ptithcm.mytiki.feature.customer.cart.CartActivity
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_add_cart.view.*
import kotlinx.android.synthetic.main.ll_cart.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.RoundingMode
import java.text.DecimalFormat


class ProductDetailActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var productDetail: Product? = null

    var checkAddcart: Boolean = false

    private val productViewModel: ProductViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child(Constant.USER)
        getData()
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
    }

    @SuppressLint("InflateParams")
    private fun addEvent() {
        ll_cart_number.setOnClickListener {
            val user: FirebaseUser? = mAuth?.currentUser
            if (user != null) {
                val intentCart = Intent(this, CartActivity::class.java)
                startActivity(intentCart)
            } else {
                val intentCart = Intent(this, AuthActivity::class.java)
                startActivity(intentCart)
            }
        }

        btn_buy_product_detail.setOnClickListener {
            checkAddcart = true
            val user: FirebaseUser? = mAuth?.currentUser
            // Check user loged in firebase yet?
            if (user != null) {
                if (productDetail?.product_count!! > 0) {
                    // Add this product into list cart
                    addCart(productDetail?.id)
                    val mBottomSheetDialog = RoundedBottomSheetDialog(this)
                    val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_cart, null)

                    //Set image in bottom dialog
                    Glide.with(applicationContext)
                        .load(productDetail?.image)
                        .into(sheetView.iv_product_add_cart)
                    sheetView.tv_product_name_add_cart.text = productDetail?.name

                    val pricesale =
                        productDetail?.price?.minus(((productDetail?.sale!! * 0.01) * productDetail?.price!!))
                    // format price sale
                    val df = DecimalFormat("#,###,###")
                    df.roundingMode = RoundingMode.CEILING
                    val priceDiscount = df.format(pricesale) + " đ"
                    sheetView.tv_product_price_addcart.text = priceDiscount

                    mBottomSheetDialog.setContentView(sheetView)
                    mBottomSheetDialog.show()

                    sheetView.btn_cancel_dialog_add_cart.setOnClickListener {
                        mBottomSheetDialog.dismiss()
                    }

                    sheetView.btn_view_cart.setOnClickListener {
                        val intent = Intent(this, CartActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, R.string.err_add_cart, Toast.LENGTH_LONG).show()
                }

            } else {// if user not login
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addCart(id: String?) {

        val user: FirebaseUser? = mAuth?.currentUser
        // Check user loged in firebase yet?
        if (user != null) {
            var i = 1// use i to exit add number of cart, because it run anyway
            mDatabase = FirebaseDatabase.getInstance()
            val query =
                mDatabase!!.reference.child(Constant.CART).child(user.uid).child(id.toString())
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        // if this cart is not exist in list cart
                        if (i == 1) {
                            // If this product exist in cart, then number ++
                            var number = ds.child(Constant.CART_NUMBER).value as Long
                            number++
                            query.child(Constant.CART_NUMBER)
                                .setValue(number)
                        }
                        i++

                    } else if (checkAddcart) {
                        i++
                        query.child(Constant.CART_ID)
                            .setValue(id)
                        query.child(Constant.CART_NUMBER)
                            .setValue(1)
                        checkAddcart = false
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)

        }
    }

    // add product into viewed product if user had login
    private fun addViewedProduct(id: String) {
        val user: FirebaseUser? = mAuth?.currentUser
        // Check user loged in firebase yet?
        if (user != null) {
            // Add product into viewed list product
            mDatabaseReference = mDatabase!!.reference
            val currentUserDb = mDatabaseReference.child(Constant.USER)
                .child(user.uid)
                .child(Constant.VIEWED_PRODUCT)

            currentUserDb.child(id).child(Constant.VIEWED_PRODUCT_ID)
                .setValue(id)
        }
    }

    private fun setData(product: Product) {
        //sset image view product
        Glide.with(applicationContext)
            .load(product.image)
            .into(iv_product_detail)

        // name of product
        tv_name_product_detail.text = product.name

        // Sale percent
        val strPercent = "-" + product.sale.toString() + "%"
        tv_discount_percent.text = strPercent

        // format price sale
        val df = DecimalFormat("#,###,###")
        df.roundingMode = RoundingMode.CEILING


        // set price for product
        val pricesale = product.price?.minus(((product.sale * 0.01) * product.price!!))
        val priceDiscount = df.format(pricesale) + " đ"
        tv_price_discount_product_detail.text = priceDiscount

        // Giá gốc của sản phẩm
        val price = df.format(product.price) + " đ"
        tv_price_product_detail.text = price
        tv_price_product_detail.paintFlags =
            tv_price_product_detail.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // Show or hide discount percent
        if (product.sale > 0) {
            tv_price_product_detail.visibility = View.VISIBLE
            tv_discount_percent.visibility = View.VISIBLE
        } else {
            tv_discount_percent.visibility = View.GONE
            tv_price_product_detail.visibility = View.GONE
        }

        // Product count >0,
        if (product.product_count!! > 0) tv_out_of_product.visibility = View.GONE
        else tv_out_of_product.visibility = View.VISIBLE

        //Set content product
        tv_content_product_detail.text = product.infor

        // set btn like selected
        setBtnLikeIsCheck(product.id)

        // Set ic like in toobar
        btn_like.setOnClickListener {
            val like = !btn_like.isSelected
            checkFavoriteProduct(product.id, like)
            btn_like.isSelected = like
        }
    }

    // Set status for btn like
    private fun setBtnLikeIsCheck(id: String?) {
        val user: FirebaseUser? = mAuth?.currentUser
        // Check user logged in firebase yet?
        if (user != null) {
            // Check this product
            val query = mDatabase!!.reference.child(Constant.USER).child(user.uid)
                .child(Constant.FAVORITE_PRODUCT).child(id!!)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    // if user haven 't add this product into favorite list, then add this
                    btn_like.isSelected = ds.exists()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }


    private fun checkFavoriteProduct(id: String?, like: Boolean) {
        val user: FirebaseUser? = mAuth?.currentUser
        if (like) {
            // Add product into favrite list product
            mDatabaseReference = mDatabase!!.reference
            val currentUserDb = mDatabaseReference.child(Constant.USER)
                .child(user?.uid.toString())
                .child(Constant.FAVORITE_PRODUCT)
            currentUserDb.child(id!!).child(Constant.FAVORITE_ID)
                .setValue(id)
        }
        else {// Del product from list
            mDatabaseReference = mDatabase!!.reference
            mDatabaseReference.child(Constant.USER)
                .child(user?.uid.toString())
                .child(Constant.FAVORITE_PRODUCT)
                .child(id!!)
                .removeValue()
        }
    }

    private fun getData() {
        // Get id product to get info
        val productID = intent.getStringExtra("id_product")
        if (productID != null) {
            //CHeck login to add viewed product
            addViewedProduct(productID)
            getProductById(productID)
        }
    }

    private fun getProductById(id: String) {
        var product: Product?
        mDatabase = FirebaseDatabase.getInstance()
        val query = mDatabase!!
            .reference
            .child(Constant.PRODUCT)
            .child(id)

        val valueEventListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(ds: DataSnapshot) {
                if (ds.exists()) {
                    product = ds.getValue(Product::class.java)
                    if (product?.del == false) {
                        productDetail = product
                        setData(product!!)
                        productDeleted.gone()
                    } else productDeleted.visible()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    fun onClickQuiteProduct(view: View) {
        finish()
    }
}
