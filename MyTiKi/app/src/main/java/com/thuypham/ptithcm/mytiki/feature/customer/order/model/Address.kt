package com.thuypham.ptithcm.mytiki.feature.customer.order.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Address(

    @SerializedName("id")
    var id: String?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("phone")
    var phone: String?,

    @SerializedName("address")
    var address: String?,

    @SerializedName("default")
    var default: Boolean?


) : Serializable