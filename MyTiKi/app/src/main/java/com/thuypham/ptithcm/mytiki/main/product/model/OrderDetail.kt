package com.thuypham.ptithcm.mytiki.main.product.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OrderDetail(
    @SerializedName("id")
    var id: String?,

    @SerializedName("product_name")
    var product_name: String?,

    @SerializedName("id_product")
    var id_product: String?,

    @SerializedName("product_count")
    var product_count: Long?,

    @SerializedName("product_price")
    var product_price: Long?,

    @SerializedName("id_order")
    var id_order: String?
) : Serializable