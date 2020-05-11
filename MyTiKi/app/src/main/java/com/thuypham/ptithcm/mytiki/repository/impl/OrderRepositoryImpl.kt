package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.OrderDetail
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.repository.OrderRepository
import com.thuypham.ptithcm.mytiki.util.Constant.ORDER
import com.thuypham.ptithcm.mytiki.util.Constant.ORDER_DATE
import com.thuypham.ptithcm.mytiki.util.Constant.ORDER_DETAIL
import com.thuypham.ptithcm.mytiki.util.Constant.ORDER_STATUS

class OrderRepositoryImpl : OrderRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun databaseRef() = firebaseDatabase?.reference
    override fun getAllOrder(): ResultData<ArrayList<Order>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListOrder = MutableLiveData<ArrayList<Order>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Order>()
        var order: Order?
        val query = databaseRef()?.child(ORDER)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        order = ds.getValue(Order::class.java)
                        order?.let { listProduct.add(it) }
                    }
                    responseListOrder.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List order are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListOrder,
            networkState = networkState
        )
    }

    override fun getAllOrderByType(type: Int): ResultData<ArrayList<Order>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListOrder = MutableLiveData<ArrayList<Order>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Order>()
        var order: Order?
        val query =
            databaseRef()?.child(ORDER)?.orderByChild(ORDER_STATUS)?.equalTo(type.toDouble())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        order = ds.getValue(Order::class.java)
                        order?.let { listProduct.add(it) }
                    }
                    responseListOrder.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List order are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListOrder,
            networkState = networkState
        )
    }

    override fun changeStatusOfOrder(type: Int, orderId: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        databaseRef()?.child(ORDER)?.child(orderId)?.child(ORDER_STATUS)?.setValue(type)
            ?.addOnCompleteListener {
                networkState.value = NetworkState.LOADED
            }
            ?.addOnFailureListener { err ->
                networkState.postValue(NetworkState.error(err.message))
            }
        return networkState
    }

    override fun getOrderByDate(type: Int?, date: String): ResultData<ArrayList<Order>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListOrder = MutableLiveData<ArrayList<Order>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<Order>()
        var order: Order?
        val query = databaseRef()?.child(ORDER)?.orderByChild(ORDER_DATE)?.equalTo(date)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        order = ds.getValue(Order::class.java)
                        if (order?.status == type?.toLong())
                            order?.let { listProduct.add(it) }
                    }
                    responseListOrder.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List Order are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListOrder,
            networkState = networkState
        )
    }

    override fun getOrderDetail(orderID: String): ResultData<ArrayList<OrderDetail>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListOrderDetail = MutableLiveData<ArrayList<OrderDetail>>()
        networkState.postValue(NetworkState.LOADING)
        val listProduct = ArrayList<OrderDetail>()
        var orderDetail: OrderDetail?
        val query = databaseRef()?.child(ORDER_DETAIL)?.child(orderID)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        orderDetail = ds.getValue(OrderDetail::class.java)
                        orderDetail?.let { listProduct.add(it) }
                    }
                    responseListOrderDetail.value = listProduct
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List order detail are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListOrderDetail,
            networkState = networkState
        )
    }

}