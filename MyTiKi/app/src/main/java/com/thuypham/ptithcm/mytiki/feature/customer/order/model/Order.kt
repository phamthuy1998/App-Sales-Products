package com.thuypham.ptithcm.mytiki.feature.customer.order.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Order (
    @SerializedName("id")
    var id: String?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("phone")
    var phone: String?,

    @SerializedName("address")
    var address: String?,

    @SerializedName("date")
    var date: String?,

    @SerializedName("price")
    var price: Long?,

    @SerializedName("status")
    var status: Long?

) : Serializable