package com.thuypham.ptithcm.mytiki.feature.customer.home.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

    abstract class BaseItem(view: View) : RecyclerView.ViewHolder(view) {

        abstract fun bind(position: Int)
    }