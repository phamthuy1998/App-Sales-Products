package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.OrderDetail
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.OrderRepository

class OrderViewModel(private val repository:OrderRepository) :ViewModel(){


    /* get all order */
    private val responseListOrder = MutableLiveData<ResultData<ArrayList<Order>>>()
    val listOrder = Transformations.switchMap(responseListOrder) {
        it.data
    }

    val networkListOrder = Transformations.switchMap(responseListOrder) {
        it.networkState
    }

    fun getAllOrder(){
        responseListOrder.value = repository.getAllOrder()
    }

    /* get all order by type */
    fun getAllOrderByType(type: Int){
        responseListOrder.value = repository.getAllOrderByType(type)
    }

    /* get order by date */
    fun getOrderByDate(type:Int?=null, date: String){
        responseListOrder.value = repository.getOrderByDate(type, date)
    }

    /* change status of order */
    private var responseChangeStatus = MutableLiveData<NetworkState>()

    fun changeStatusOrder(type: Int, orderID: String){
        responseChangeStatus = repository.changeStatusOfOrder(type, orderID)
    }

    /* get order detail*/
    private var responseOrderDetail = MutableLiveData<ResultData<ArrayList<OrderDetail>>>()

    val listOrderDetail = Transformations.switchMap(responseOrderDetail) {
        it.data
    }

    val networkOrderDetail = Transformations.switchMap(responseOrderDetail) {
        it.networkState
    }

    fun getOrderDetail(orderID: String){
        responseOrderDetail.value = repository.getOrderDetail(orderID)
    }
}