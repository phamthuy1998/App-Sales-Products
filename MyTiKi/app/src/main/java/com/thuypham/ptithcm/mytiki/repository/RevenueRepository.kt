package com.thuypham.ptithcm.mytiki.repository

import com.thuypham.ptithcm.mytiki.data.ResultData
import com.thuypham.ptithcm.mytiki.data.Revenue

interface RevenueRepository {
    fun getRevenueInMonth(date: Int? = null, month: Int, year: Int): ResultData<ArrayList<Revenue>>
}