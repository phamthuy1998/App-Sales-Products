package com.thuypham.ptithcm.mytiki.feature.employee.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import com.thuypham.ptithcm.mytiki.data.Category
import com.thuypham.ptithcm.mytiki.databinding.ItemCategoryEmployeeBinding

class CategoryEmployeeAdapter(
    private var listProductSale: MutableList<Category> = arrayListOf(),
    private var onItemClick: (category: Category) -> Unit
) : DynamicSearchAdapter<Category>(listProductSale) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PlantViewHolder(
            ItemCategoryEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listProductSale.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PlantViewHolder).bind(listProductSale[position])
    }

    fun setCategoryList(list: ArrayList<Category>) {
        listProductSale.apply {
            clear()
            addAll(list)
            updateData(list)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Category>) {
        listProductSale.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listProductSale.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    inner class PlantViewHolder(
        private val binding: ItemCategoryEmployeeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category) {
            binding.apply {
                category = item
                executePendingBindings()
                itemProduct.setOnClickListener {  onItemClick(item)  }
            }
        }
    }
}