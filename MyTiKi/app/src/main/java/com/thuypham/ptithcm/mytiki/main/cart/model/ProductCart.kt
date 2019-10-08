package com.thuypham.ptithcm.mytiki.main.cart.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductCart(
    @SerializedName("id")
    var id: String?,
    @SerializedName("number")
    var product_count: Long
) : Serializable