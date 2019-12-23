package com.thuypham.ptithcm.mytiki.main.fragment.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.category.activity.ProductOfCategory
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import com.thuypham.ptithcm.mytiki.main.fragment.home.fragment.HomeFragment
import kotlinx.android.synthetic.main.item_category.view.*
import kotlin.collections.ArrayList

class CategoryAdapterHome(
    private var items: ArrayList<Category>,
    private val context: HomeFragment
) : RecyclerView.Adapter<BaseItem>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {

        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_category, viewGroup, false);
        return CategoryViewholder(view)
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 1 else items.size
    }

    override fun onBindViewHolder(holder: BaseItem, position: Int) {
        holder.bind(position)
    }

    inner class CategoryViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            if (!items.isEmpty()) {
                val category: Category = items[position]
                itemView.tv_name_category.text = category.nameCategory
                println("ten cate adap ${category.nameCategory}")
                //sset image view category
                Glide.with(itemView)
                    .load(category.image)
                    .into(itemView.iv_image_category)

                // Intent to ProductOfCategory
                itemView.ll_item_category.setOnClickListener {
                    var intent = Intent(context.context, ProductOfCategory::class.java)
                    intent.putExtra("id_category", category.idCategory)
                    intent.putExtra("name_category", category.nameCategory)
                    context!!.startActivity(intent)
                }
            }

        }

    }
}


