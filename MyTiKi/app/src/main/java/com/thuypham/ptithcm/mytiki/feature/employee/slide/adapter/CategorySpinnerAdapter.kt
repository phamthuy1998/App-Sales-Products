package com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.databinding.ItemCategorySpinnerBinding


class CategorySpinnerAdapter(
    private var listCategory: MutableList<Category> = arrayListOf()
) : BaseAdapter() {

    fun setCategoryList(list: ArrayList<Category>) {
        listCategory.apply {
            clear()
            addAll(list)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listCategory.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    inner class CategoryViewHolder(
        val view: View,
        val binding: ItemCategorySpinnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            binding.apply {
                category = item
                executePendingBindings()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: CategoryViewHolder

        if (convertView == null) {
            val itemBinding: ItemCategorySpinnerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent!!.context),
                R.layout.item_category_spinner,
                parent,
                false
            )
            holder = CategoryViewHolder(itemBinding.root, itemBinding)
            holder.view.tag = holder
        } else {
            holder = convertView.tag as CategoryViewHolder
        }
        val categoryItem = listCategory[position]
        holder.bind(categoryItem)
        return holder.view
    }

    override fun getItem(position: Int): Any = listCategory[position]

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = listCategory.size
}