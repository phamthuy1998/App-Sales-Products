package com.thuypham.ptithcm.mytiki.services

import com.thuypham.ptithcm.mytiki.data.ProductAdd
import com.thuypham.ptithcm.mytiki.util.Constant
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers(
        "Authorization: key=" + Constant.Authorization,
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    suspend fun sendNotificationAsync(@Body product: ProductAdd)
}