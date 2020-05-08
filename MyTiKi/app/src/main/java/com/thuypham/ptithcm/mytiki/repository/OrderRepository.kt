package com.thuypham.ptithcm.mytiki.repository

import androidx.lifecycle.MutableLiveData
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.OrderDetail
import com.thuypham.ptithcm.mytiki.data.ResultData

interface OrderRepository {
    fun getAllOrderByType(type: Int): ResultData<ArrayList<Order>>
    fun changeStatusOfOrder(type: Int, orderId:String): MutableLiveData<NetworkState>
    fun getOrderByDate(type:Int, date: String): ResultData<ArrayList<Order>>
    fun getOrderDetail(orderID: String): ResultData<ArrayList<OrderDetail>>
}