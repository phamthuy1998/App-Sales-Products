package com.thuypham.ptithcm.mytiki.feature.employee.order.adaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.databinding.ItemOrderEmployeeBinding

class OrderEmployeeAdapter(
    private var listProductSale: MutableList<Order> = arrayListOf(),
    private var onItemClick: (order: Order) -> Unit
) : DynamicSearchAdapter<Order>(listProductSale) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        OrderViewHolder(
            ItemOrderEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listProductSale.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OrderViewHolder).bind(listProductSale[position])
    }

    fun setOrderList(list: ArrayList<Order>) {
        listProductSale.apply {
            clear()
            addAll(list)
            updateData(list)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Order>) {
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

    inner class OrderViewHolder(
        private val binding: ItemOrderEmployeeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Order) {
            binding.apply {
                order = item
                executePendingBindings()
                itemOrder.setOnClickListener { onItemClick(item)  }
            }
        }
    }
}