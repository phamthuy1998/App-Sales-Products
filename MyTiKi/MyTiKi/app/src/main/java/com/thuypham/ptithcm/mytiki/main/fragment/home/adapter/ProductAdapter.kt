package com.thuypham.ptithcm.mytiki.main.fragment.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import com.thuypham.ptithcm.mytiki.main.fragment.home.fragment.HomeFragment
import com.thuypham.ptithcm.mytiki.main.product.activity.ProductDetailActivity
import com.thuypham.ptithcm.mytiki.main.product.model.Product
import kotlinx.android.synthetic.main.item_product.view.*
import kotlinx.android.synthetic.main.item_product_sale.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class ProductAdapter(private var items: ArrayList<Product>, private val context: Context) : RecyclerView.Adapter<BaseItem>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(R.layout.item_product, viewGroup, false);
        return ProductViewholder(view)
    }

    inner class ProductViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val product = items[position]

            itemView.tv_name_product.text = product.name

            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val pricesale = product.price?.minus(((product.sale*0.01)* product.price!!))
            val price = df.format(pricesale) + " đ"
            itemView.tv_price_product.text = price
            if(product.sale>0)itemView.tv_sale_product.visibility= View.VISIBLE
            else itemView.tv_sale_product.visibility= View.GONE
            val sale = "-" + product.sale.toString() + "%"
            itemView.tv_sale_product.text = sale
            println("gia sp : $price")
            println("name sp : ${product.name}")

            //sset image view product
            Glide.with(itemView)
                    .load(product.image)
                    .into(itemView.iv_product)

            itemView.ll_product.setOnClickListener {
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