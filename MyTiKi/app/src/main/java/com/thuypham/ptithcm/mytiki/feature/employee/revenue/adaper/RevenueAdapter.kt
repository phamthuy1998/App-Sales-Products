package com.thuypham.ptithcm.mytiki.feature.employee.revenue.adaper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.data.Revenue
import com.thuypham.ptithcm.mytiki.databinding.ItemRevenueBinding

class RevenueAdapter(
    private var listRevenue: MutableList<Revenue> = arrayListOf(),
    private var onItemClick: (date: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        RevenueViewHolder(
            ItemRevenueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listRevenue.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RevenueViewHolder).bind(listRevenue[position])
    }

    fun setRevenueList(arr: MutableList<Revenue>) {
        listRevenue.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listRevenue.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    inner class RevenueViewHolder(
        private val binding: ItemRevenueBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Revenue) {
            binding.apply {
                revenue = item
                executePendingBindings()
                itemRevenue.setOnClickListener { item.date?.let { it1 -> onItemClick(it1) } }
            }
        }
    }
}