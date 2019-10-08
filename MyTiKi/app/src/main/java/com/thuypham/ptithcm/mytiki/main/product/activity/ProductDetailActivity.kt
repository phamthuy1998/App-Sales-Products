package com.thuypham.ptithcm.mytiki.main.product.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.activity_product_detail.*
import com.thuypham.ptithcm.mytiki.main.fragment.user.login.activity.SignInUpActivity
import java.math.RoundingMode
import java.text.DecimalFormat
import android.graphics.Paint
import androidx.constraintlayout.widget.ConstraintLayout
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.thuypham.ptithcm.mytiki.main.cart.activity.CartActivity
import kotlinx.android.synthetic.main.bottom_sheet_add_cart.view.*


class ProductDetailActivity : AppCompatActivity() {

    lateinit var mDatabaseReference: DatabaseReference
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var productDetail: Product? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        getData()
        addEvent()
    }

    private fun addEvent() {
        btn_buy_product_detail.setOnClickListener() {
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

            sheetView.btn_cancel_dialog_add_cart.setOnClickListener() {
                mBottomSheetDialog.dismiss()
            }

            sheetView.btn_view_cart.setOnClickListener() {
                var intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }

            addCart(productDetail?.id)
        }
    }

    private fun addCart(id: String?) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();

        // Check user loged in firebase yet?
        if (user != null) {

            var i = 1// use i to exit add number of cart, because it run anyway
            mDatabase = FirebaseDatabase.getInstance()
            println("id: " + id)
            println("userid: " + user.uid)
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.CART)
                .child(user.uid)
                .child(id.toString())
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    if (ds.exists()) {
                        if (i == 1) {
                            println("ton tai")
                            // If this product exist in cart, then number++
                            var number = ds.child(PhysicsConstants.CART_NUMBER).value as Long
                            println("number:" + number)
                            number++
                            query.child(PhysicsConstants.CART_NUMBER)
                                .setValue(number)
                        }
                        i++

                    } else {
                        i++
                        println("khong ton tai")
                        query.child(PhysicsConstants.CART_ID)
                            .setValue(id)
                        query.child(PhysicsConstants.CART_NUMBER)
                            .setValue(1)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("lay 1 sp k thanh cong")
                }
            }
            query.addValueEventListener(valueEventListener)

        }
    }

    // add product into viewed product if user had login
    private fun addViewedProduct(id: String) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            // Add product into viewed list product
            mDatabaseReference = mDatabase!!.reference
            val currentUserDb = mDatabaseReference.child(PhysicsConstants.USERS)
                .child(user.uid)
                .child(PhysicsConstants.VIEWED_PRODUCT)

            currentUserDb.child(id).child(PhysicsConstants.VIEWED_PRODUCT_ID)
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
        tv_price_product_detail.setPaintFlags(tv_price_product_detail.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        // Show or hide discount percent
        if (product.sale > 0) tv_discount_percent.visibility = View.VISIBLE
        else tv_discount_percent.visibility = View.GONE

        // Product count >0,
        if (product.product_count > 0) tv_out_of_product.visibility = View.GONE
        else tv_out_of_product.visibility = View.VISIBLE

        //Set content product
        tv_content_product_detail.text = product.infor

        // set btn like selected
        setBtnLikeIsCheck(product.id)

        // Set ic like in toobar
        btn_like.setOnClickListener() {
            val like = !btn_like.isSelected
            var user: FirebaseUser? = mAuth?.getCurrentUser();
            if (user != null) {
                checkFavoriteProduct(product.id, like)
                btn_like.isSelected = like
            } else {
                // If user haven't login yet, intent to sign in
                val intent = Intent(baseContext, SignInUpActivity::class.java)
                startActivity(intent)
                println("dang nhap xong")
                user = mAuth?.getCurrentUser()
                if (user != null) {
                    println("user khac null")
                    checkFavoriteProduct(product.id, like)
                    btn_like.isSelected = like
                } else {
                    println("dang nhap k thanh cong")
                }
            }
        }
    }

    // Set status for btn like
    private fun setBtnLikeIsCheck(id: String?) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            // Check this product
            val query = mDatabase!!
                .reference
                .child(PhysicsConstants.USERS)
                .child(user.uid)
                .child(PhysicsConstants.FAVORITE_PRODUCT)
                .child(id!!)

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    // if user haven 't add this product into favorite list, then add this
                    if (!ds.exists()) {
                        btn_like.isSelected = false;
                    } else {
                        btn_like.isSelected = true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("lay 1 sp k thanh cong")
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }


    private fun checkFavoriteProduct(id: String?, like: Boolean) {
        val user: FirebaseUser? = mAuth?.getCurrentUser();
        // Check user loged in firebase yet?
        if (user != null) {
            if (like) {
                // Add product into favrite list product
                mDatabaseReference = mDatabase!!.reference
                val currentUserDb = mDatabaseReference.child(PhysicsConstants.USERS)
                    .child(user.uid)
                    .child(PhysicsConstants.FAVORITE_PRODUCT)
                currentUserDb.child(id!!).child(PhysicsConstants.FAVORITE_ID)
                    .setValue(id)
            } else {// Del product from list
                mDatabaseReference = mDatabase!!.reference
                val currentUserDb = mDatabaseReference.child(PhysicsConstants.USERS)
                    .child(user.uid)
                    .child(PhysicsConstants.FAVORITE_PRODUCT)
                    .child(id!!)
                    .removeValue()
            }

        }
    }

    private fun getData() {
        // Get id product to get infor
        val id_product = intent.getStringExtra("id_product")
        if (id_product != null) {
            //CHeck login to add viewd product
            addViewedProduct(id_product)
            getProductById(id_product)
        }
    }

    private fun getProductById(id: String) {
        var product: Product
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
                    productDetail = product
                    setData(product)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("lay 1 sp k thanh cong")
            }
        }
        query.addValueEventListener(valueEventListener)

    }


    fun onClickQuiteProduct(view: View) {
        finish()
    }
}
