package com.thuypham.ptithcm.mytiki.main.product.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Order (
    @SerializedName("id")
    var id: String?,

    @SerializedName("date")
    var date: String?,

    @SerializedName("price")
    var price: Long?,

    @SerializedName("status")
    var status: Long?

) : Serializable