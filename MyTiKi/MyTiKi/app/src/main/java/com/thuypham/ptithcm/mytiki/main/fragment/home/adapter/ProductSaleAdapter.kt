package com.thuypham.ptithcm.mytiki.main.fragment.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.item_product_sale.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import android.content.Intent
import com.thuypham.ptithcm.mytiki.main.product.activity.ProductDetailActivity


class ProductSaleAdapter(private var items: ArrayList<Product>, private val context: Context) : RecyclerView.Adapter<BaseItem>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(com.thuypham.ptithcm.mytiki.R.layout.item_product_sale, viewGroup, false);
        return ProductSaleViewholder(view)
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

            // name product sale
            itemView.tv_name_product_sale.text = product.name

            // format price sale
            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val pricesale = product.price?.minus(((product.sale*0.01)* product.price!!))
            val price = df.format(pricesale) + " đ"
            itemView.tv_price_product_favorite.text = price

            //% sale
            val sale = product.sale.toString() + "%"
            itemView.tv_product_sale_home.text = sale
            println("gia sp sale : $price")
            println("name sp : ${product.name}")

            //sset image view product
            Glide.with(itemView)
                    .load(product.image)
                    .into(itemView.iv_image_product_sale)

            itemView.ll_product_sale.setOnClickListener {
                var intent=Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("id_product",product.id)
                context.startActivity(intent)

            }
        }

    }
}