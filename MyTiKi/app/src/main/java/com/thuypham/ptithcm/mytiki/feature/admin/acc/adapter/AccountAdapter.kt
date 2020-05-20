package com.thuypham.ptithcm.mytiki.feature.admin.acc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import com.thuypham.ptithcm.mytiki.data.User
import com.thuypham.ptithcm.mytiki.databinding.ItemAccountBinding

class AccountAdapter(
    private var listUser: MutableList<User> = arrayListOf(),
    private var onItemClick: (user: User) -> Unit
) : DynamicSearchAdapter<User>(listUser) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        UserViewHolder(
            ItemAccountBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(listUser[position])
    }

    fun setUserList(list: ArrayList<User>) {
        listUser.apply {
            clear()
            addAll(list)
            updateData(list)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<User>) {
        listUser.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listUser.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    inner class UserViewHolder(
        private val binding: ItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User) {
            binding.apply {
                user = item
                executePendingBindings()
                itemAccount.setOnClickListener { onItemClick(item)  }
            }
        }
    }
}