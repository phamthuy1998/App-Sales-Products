package com.thuypham.ptithcm.mytiki.repository.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.thuypham.ptithcm.mytiki.data.NetworkState
import com.thuypham.ptithcm.mytiki.data.Order
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Revenue
import com.thuypham.ptithcm.mytiki.repository.RevenueRepository
import com.thuypham.ptithcm.mytiki.util.Constant
import com.thuypham.ptithcm.mytiki.util.formatNumber
import com.thuypham.ptithcm.mytiki.util.getDayInMonth

class RevenueRepositoryImpl : RevenueRepository {
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun databaseRef() = firebaseDatabase?.reference

    override fun getRevenueInMonth(
        date: Int?,
        month: Int,
        year: Int
    ): ResultData<ArrayList<Revenue>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListProduct = MutableLiveData<ArrayList<Revenue>>()
        networkState.postValue(NetworkState.LOADING)

        val listRevenue = ArrayList<Revenue>()
        val listOrder = ArrayList<Order>()
        var order: Order?
        val totalDateInMoth: Int = date ?: getDayInMonth(month, year)

        var query: Query?
        var price: Long
        var dateStr: String
        for (dateOrder in totalDateInMoth downTo 1 step 1) {
            dateStr = formatNumber(dateOrder) + "/" + formatNumber(month) + "/" + formatNumber(year)

//            query = databaseRef()?.child(Constant.ORDER)
//                ?.orderByChild(Constant.ORDER_DATE_SEARCH)?.equalTo(dateStr)
//            val valueEventListener = object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        price = 0
//                        for (ds in dataSnapshot.children) {
//                            order = ds.getValue(Order::class.java)
//                            order?.let { listOrder.add(it) }
//                            price += order?.price ?: 0
//                        }
//                        listRevenue.add(Revenue(dateStr, listOrder.size, price))
//                        if (dateOrder == 1) {
//                            responseListProduct.value = listRevenue
//                            networkState.postValue(NetworkState.LOADED)
//                        }
//                    } else {
//                        listRevenue.add(Revenue(dateStr, 0, 0))
//                        if (dateOrder == 1) {
//                            responseListProduct.value = listRevenue
//                            networkState.postValue(NetworkState.LOADED)
//                        }
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    listRevenue.add(Revenue(dateStr, 0, 0))
//                    if (dateOrder == 1) {
//                        responseListProduct.value = listRevenue
//                        networkState.postValue(NetworkState.LOADED)
//                    }
//                }
//            }
//            query?.addValueEventListener(valueEventListener)
        }

        return ResultData(
            data = responseListProduct,
            networkState = networkState
        )
    }

    private fun getOrderRevenueByDate(dateStr: String) : MutableLiveData<Revenue>{
        val revenueLiveData = MutableLiveData<Revenue>()
        val listOrder = ArrayList<Order>()
        var price: Long
        var order: Order?
        var revenue: Revenue?
        val query = databaseRef()?.child(Constant.ORDER)
            ?.orderByChild(Constant.ORDER_DATE_SEARCH)?.equalTo(dateStr)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    price = 0
                    for (ds in dataSnapshot.children) {
                        order = ds.getValue(Order::class.java)
                        order?.let { listOrder.add(it) }
                        price += order?.price ?: 0
                    }
                    revenue = Revenue(dateStr, listOrder.size,price)
                    revenueLiveData.value = revenue
                } else {
                    revenue=Revenue(dateStr, 0, 0)
                    revenueLiveData.value = revenue
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                revenue=Revenue(dateStr, 0, 0)
                revenueLiveData.value = revenue
            }
        }
        query?.addValueEventListener(valueEventListener)

        return revenueLiveData
    }

}