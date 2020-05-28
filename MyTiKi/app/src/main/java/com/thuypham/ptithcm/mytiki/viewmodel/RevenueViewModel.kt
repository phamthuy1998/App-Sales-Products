package com.thuypham.ptithcm.mytiki.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Revenue
import com.thuypham.ptithcm.mytiki.repository.RevenueRepository

class RevenueViewModel(private val repository: RevenueRepository) : ViewModel() {
/*
    *//* get all revenue in month *//*
    private val responseListRevenueInMonth = MutableLiveData<ResultData<Revenue>>()
    val listRevenue = Transformations.switchMap(responseListRevenueInMonth) {
        it.data
    }

    val networkListRevenue = Transformations.switchMap(responseListRevenueInMonth) {
        it.networkState
    }

    fun getAllRevenueInMonth(dateString: String) {
        responseListRevenueInMonth.value = repository.getRevenueByDate(dateString)
    }*/


    /* get all revenue in month */
    private val responseListRevenueInMonth = MutableLiveData<ResultData<ArrayList<Revenue>>>()
    val listRevenue = Transformations.switchMap(responseListRevenueInMonth) {
        it.data
    }

    val networkListRevenue = Transformations.switchMap(responseListRevenueInMonth) {
        it.networkState
    }

    fun getAllRevenueInMonth(date: Int? = null, month: Int, year: Int) {
        responseListRevenueInMonth.value = repository.getRevenueInMonth(date, month, year)
    }
}