package com.thuypham.ptithcm.mytiki.main.cart.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductCartDetail (

    @SerializedName("id")
    var id: String?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("price")
    var price: Long?,

    var number_product: Long?,


    @SerializedName("image")
    var image: String?,

    @SerializedName("product_count")
    var product_count: Long,

    @SerializedName("id_category")
    var id_category: String?,

    @SerializedName("sale")
    var sale: Long


) : Serializable