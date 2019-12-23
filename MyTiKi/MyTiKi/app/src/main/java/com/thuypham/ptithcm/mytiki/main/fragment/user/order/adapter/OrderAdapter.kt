package com.thuypham.ptithcm.mytiki.main.fragment.user.order.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.mytiki.R
import com.thuypham.ptithcm.mytiki.main.fragment.home.adapter.BaseItem
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.activity.OrderDetailActivity
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.Order
import com.thuypham.ptithcm.mytiki.main.fragment.user.order.model.OrderDetail
import kotlinx.android.synthetic.main.item_order.view.*

class OrderAdapter(
    private var items: ArrayList<Order>,
    private var orderDetails: ArrayList<OrderDetail>,
    private val context: Context
) : RecyclerView.Adapter<BaseItem>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseItem {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_order, viewGroup, false);
        return ProductViewholder(view)
    }

    inner class ProductViewholder(view: View) : BaseItem(view) {
        override fun bind(position: Int) {
            val order = items[position]

            // name order
            var name = ""
            // get name in list order detail to create string name include anothor names which have the same order id
            for(o in orderDetails){
                if (o.id_order.equals(order.id)){
                    name += o.product_name+", "
                }
            }
            Log.d("sizemangad", orderDetails.size.toString())
            itemView.tv_name_item_order.text = name

            // set order id
            itemView.tv_item_order_detail_id.text = order.id
            // set order date
            itemView.tv_item_order_detail_date.text = order.date
            // set status for order
            var status = ""
            // have recieved
            if (order.status == 1.toLong()) {
                status = context.getString(R.string.status_1)
                itemView.iv_order_ic_status.setImageResource(R.drawable.ic_circle_arrow);
            }
            // shipping
            else if (order.status == 2.toLong()) {
                status = context.getString(R.string.status_2)
                itemView.iv_order_ic_status.setImageResource(R.drawable.ic_shipping);
            }
            // order success
            else if (order.status == 3.toLong()) {
                status = context.getString(R.string.status_3)
                itemView.iv_order_ic_status.setImageResource(R.drawable.ic_success);
            }
            // order cancel
            else if (order.status == 4.toLong()) {
                status = context.getString(R.string.status_4)
                itemView.iv_order_ic_status.setImageResource(R.drawable.ic_error);
            }
            itemView.tv_item_order_status.text = status

            itemView.ll_item_order.setOnClickListener(){
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra("order_id", order.id)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseItem, position: Int) {
        holder.bind(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
}