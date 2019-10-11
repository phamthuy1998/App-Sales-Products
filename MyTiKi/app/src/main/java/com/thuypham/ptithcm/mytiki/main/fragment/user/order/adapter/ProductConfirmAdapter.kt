package com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.help.PhysicsConstants
import com.thuypham.ptithcm.mytiki.help.isPhoneValid
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCart
import com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model.ProductCartDetail
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.BaseItem
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Address
import kotlinx.android.synthetic.main.dialog_add_new_address.*
import kotlinx.android.synthetic.main.item_address.view.*
import kotlinx.android.synthetic.main.item_product_cart.view.*
import kotlinx.android.synthetic.main.item_product_confirm.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class ProductConfirmAdapter(
    private var items: ArrayList<ProductCartDetail>,
    private val context: Context
) : RecyclerView.Adapter<BaseItem>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_product_confirm, viewGroup, false);
        return ProductViewholder(view)
    }

    inner class ProductViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val product = items[position]

            //sset image view product
            Glide.with(itemView)
                .load(product.image)
                .into(itemView.iv_product_conf_order)

            // name product viewed
            itemView.tv_name_product_conf_order.text = product.name

            // format price viewed
            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val pricesale = product.price?.minus(((product.sale * 0.01) * product.price!!))
            val price = df.format(pricesale) + " đ"
            itemView.tv_price_product_conf_order.text = price

            val num_of_pr = "x" + product.number_product
            itemView.tv_number_product_confirm.text = num_of_pr
        }

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
}