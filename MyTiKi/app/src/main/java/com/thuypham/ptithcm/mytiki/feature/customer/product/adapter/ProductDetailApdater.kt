package com.thuypham.ptithcm.mytiki.feature.customer.product.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.feature.customer.home.adapter.BaseItem
import com.thuypham.ptithcm.mytiki.feature.customer.product.ProductDetailActivity
import kotlinx.android.synthetic.main.item_product_favorite.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class ProductDetailApdater(
    private var items: ArrayList<Product>,
    private val context: Context
) : RecyclerView.Adapter<BaseItem>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_product_favorite, viewGroup, false);
        return ProductViewholder(view)
    }

    inner class ProductViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val product = items[position]

            itemView.tv_name_product_favorite.text = product.name

            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val price = df.format(product.price) + " đ"
            itemView.tv_price_product_favorite.text = price
            val sale = "-" + product.sale.toString() + "%"
            itemView.tv_sale_like.text = sale

            //sset image view product
            Glide.with(itemView)
                .load(product.image)
                .into(itemView.iv_pr_like)

//            // Show or hide text view out of product
//            if (product.product_count > 0.0) itemView.tv_like_out_product.visibility = View.GONE
//            else itemView.tv_like_out_product.visibility = View.VISIBLE

            // set on click item product
            itemView.ll_product_like.setOnClickListener {
                var intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("id_product", product.id)
                context.startActivity(intent)
            }
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