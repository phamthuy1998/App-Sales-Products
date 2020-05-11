package com.thuypham.ptithcm.mytiki.feature.employee.product.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import com.thuypham.ptithcm.mytiki.data.Product
import com.thuypham.ptithcm.mytiki.databinding.ItemProductEmployeeBinding

class ProductEmployeeAdapter(
    private var listProductSale: MutableList<Product> = arrayListOf(),
    private var onProductClick: (id: String) -> Unit
) : DynamicSearchAdapter<Product>(listProductSale) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PlantViewHolder(
            ItemProductEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listProductSale.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PlantViewHolder).bind(listProductSale[position])
    }

    fun setProductList(list: ArrayList<Product>) {
        listProductSale.apply {
            clear()
            addAll(list)
            updateData(list)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Product>) {
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
        private val binding: ItemProductEmployeeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.apply {
                product = item
                executePendingBindings()
                itemProduct.setOnClickListener { item.id?.let { it1 -> onProductClick(it1) } }
            }
        }
    }
}