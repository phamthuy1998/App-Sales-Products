package com.thuypham.ptithcm.mytiki.feature.customer.cart.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.ProductCartDetail
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.BaseItem
import com.thuypham.ptithcm.mytiki.feature.customer.product.ProductDetailActivity
import com.thuypham.ptithcm.mytiki.util.Constant
import kotlinx.android.synthetic.main.item_product_cart.view.*
import java.math.RoundingMode
import java.text.DecimalFormat


class ProductCartAdapter(
    private var items: ArrayList<ProductCartDetail>,
    private val context: Context
) :
    RecyclerView.Adapter<BaseItem>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_product_cart, viewGroup, false);
        return ProductSaleViewholder(view)
    }

    fun notifidataSetCahnge(){
        notifidataSetCahnge()
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItem, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    inner class ProductSaleViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val product = items[position]

            //sset image view product
            Glide.with(itemView)
                .load(product.image)
                .into(itemView.iv_product_cart)

            // name product viewed
            itemView.tv_name_product_cart.text = product.name

            // format price viewed
            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val pricesale = product.price?.minus(((product.sale!! * 0.01) * product.price!!))
            val price = df.format(pricesale) + " đ"
            itemView.tv_price_product_cart.text = price

            var num_of_pr = product.number_product
            itemView.tv_number_pr_cart.text = num_of_pr.toString()

            itemView.ll_product_cart.setOnClickListener {
                var intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("id_product", product.id)
                context.startActivity(intent)
            }

            // button minus click
            itemView.btn_minus.setOnClickListener() {
                if (product.number_product!! > 1) {
                    num_of_pr = num_of_pr?.minus(1);
                    product.number_product = num_of_pr
                    // set value on firebase, num--
                    setValueNumProductCart(product.id!!, false)
                    itemView.tv_number_pr_cart.text = num_of_pr.toString()
                }
            }
            // button plus click
            itemView.btn_plus.setOnClickListener() {
                if (product.number_product!! < 20 && product.product_count!! > product.number_product!!) {
                    num_of_pr = num_of_pr?.plus(1)
                    product.number_product = num_of_pr
                    // set value on firebase, num++
                    setValueNumProductCart(product.id!!, true)
                    itemView.tv_number_pr_cart.text = num_of_pr.toString()
                } else if (product.number_product!! > 20) {
                    Toast.makeText(context, R.string.plus_cart_error, Toast.LENGTH_LONG).show()
                } else if (product.product_count!! < product.number_product!!) {
                    Toast.makeText(context, R.string.plus_cart_error_not_enough, Toast.LENGTH_LONG)
                        .show()
                }
            }

            itemView.btn_del_item_cart.setOnClickListener() {
                deleteProductFromCart(product.id, product)
            }

            Log.d("text123", items.size.toString())

        }
    }

    private fun deleteProductFromCart(
        id: String?,
        product: ProductCartDetail
    ) {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.getCurrentUser();
        val uid = user!!.uid
        var mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
        var mDatabaseReference: DatabaseReference = mDatabase!!.reference
        if (id != null) {
            val currentUserDb = mDatabaseReference.child(Constant.CART)
                .child(user.uid)
                .child(id)

            if(currentUserDb!=null){
                currentUserDb.removeValue()
                items.remove(product)
                notifyDataSetChanged()
            }

        }
    }

    fun setValueNumProductCart(idProduct: String, add: Boolean) {
        val mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.getCurrentUser();
        if (user != null) {
            // Check this product
            val query = mDatabase!!
                .reference
                .child(Constant.CART)
                .child(user.uid)
                .child(idProduct)

            var i = 1// use i to exit add number of cart, because it run anyway

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    // if user haven 't add this product into favorite list, then add this
                    if (ds.exists()) {
                        if (i == 1) {
                            var number = ds.child(Constant.CART_NUMBER).value as Long
                            if (add == true) {
                                // If this product exist in cart, then number++
                                println("number:" + number)
                                number++
                                query.child(Constant.CART_NUMBER)
                                    .setValue(number)
                            } else {
                                // If this product exist in cart, then number--
                                println("number:" + number)
                                number--
                                query.child(Constant.CART_NUMBER)
                                    .setValue(number)
                            }
                        }
                        i++
                        notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("lay 1 sp k thanh cong")
                }
            }
            query.addValueEventListener(valueEventListener)
        }
    }

}