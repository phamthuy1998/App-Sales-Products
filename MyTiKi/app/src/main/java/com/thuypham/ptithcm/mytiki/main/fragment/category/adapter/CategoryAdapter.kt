package com.thuypham.ptithcm.mytiki.main.fragment.category.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.category.activity.ProductOfCategory
import com.thuypham.ptithcm.mytiki.main.fragment.category.model.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter() : BaseAdapter() {

    var categoryList = ArrayList<Category>()
    var context: Context? = null

    constructor(context: Context, categoryList: ArrayList<Category>) : this() {
        this.context = context
        this.categoryList = categoryList
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val category = this.categoryList[position]

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var categoryView = inflator.inflate(R.layout.item_category, null)

        categoryView.tv_name_category.text = category.nameCategory
        //sset image view category
        Glide.with(categoryView)
            .load(category.image)
            .into(categoryView.iv_image_category)

        // Intent to ProductOfCategory
        categoryView.ll_item_category.setOnClickListener {
            var intent = Intent(context, ProductOfCategory::class.java)
            intent.putExtra("id_category", category.idCategory)
            intent.putExtra("name_category", category.nameCategory)
            context!!.startActivity(intent)
        }
        return categoryView
    }

    override fun getItem(p0: Int): Any {
        return categoryList[p0]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return categoryList.size
    }

}