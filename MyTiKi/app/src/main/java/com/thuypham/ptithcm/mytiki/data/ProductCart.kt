package com.thuypham.ptithcm.mytiki.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductCart(
    @SerializedName("id")
    var id: String?,
    @SerializedName("number")
    var product_count: Long
) : Serializable