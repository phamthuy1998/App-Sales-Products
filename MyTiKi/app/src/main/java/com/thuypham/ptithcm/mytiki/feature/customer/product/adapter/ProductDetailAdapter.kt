package com.thuypham.ptithcm.mytiki.feature.customer.product.adapter

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

class ProductDetailAdapter() : RecyclerView.Adapter<BaseItem>() {

    private var items: ArrayList<Product> = arrayListOf()

    fun setData(listProduct: List<Product>?) {
        if(listProduct==null) return
        items.apply {
            clear()
            addAll(listProduct)
            notifyDataSetChanged()
        }
    }

    fun addProduct(product: Product){
        items.add(product)
        notifyItemChanged(items.size-1)
    }

    fun removeAllData(){items.clear(); notifyDataSetChanged()}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_product_favorite, viewGroup, false);
        return ProductViewHolder(view)
    }

    inner class ProductViewHolder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val product = items[position]

            itemView.tv_name_product_favorite.text = product.name

            val df = DecimalFormat("#,###,###")
            df.roundingMode = RoundingMode.CEILING

            // set price for product
            val pricesale = product.price?.minus(((product.sale*0.01)* product.price!!))
            val price = df.format(pricesale) + " đ"
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
                val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                intent.putExtra("id_product", product.id)
                itemView.context.startActivity(intent)
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