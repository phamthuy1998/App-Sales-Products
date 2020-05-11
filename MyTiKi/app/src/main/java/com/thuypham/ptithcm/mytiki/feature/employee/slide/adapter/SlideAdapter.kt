package com.thuypham.ptithcm.mytiki.feature.employee.slide.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import com.thuypham.ptithcm.mytiki.data.Slide
import com.thuypham.ptithcm.mytiki.databinding.ItemSlideBinding

class SlideAdapter(
    private var listSlide: MutableList<Slide> = arrayListOf(),
    private var onItemClick: (id: String) -> Unit
) : DynamicSearchAdapter<Slide>(listSlide) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SlideViewHolder(
            ItemSlideBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listSlide.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SlideViewHolder).bind(listSlide[position])
    }

    fun setSlideList(list: ArrayList<Slide>) {
        listSlide.apply {
            clear()
            addAll(list)
            updateData(list)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Slide>) {
        listSlide.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listSlide.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    inner class SlideViewHolder(
        private val binding: ItemSlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Slide) {
            binding.apply {
                slide = item
                executePendingBindings()
                itemSlide.setOnClickListener { item.id?.let { it1 -> onItemClick(it1) } }
            }
        }
    }
}