package com.thuypham.ptithcm.mytiki.main.product.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Product(

        @SerializedName("id")
        var id: String?,

        @SerializedName("name")
        var name: String?,

        @SerializedName("price")
        var price: Long?,

        @SerializedName("image")
        var image: String?,

        @SerializedName("infor")
        var infor: String?,

        @SerializedName("product_count")
        var product_count: Long,

        @SerializedName("id_category")
        var id_category: String?,

        @SerializedName("sale")
        var sale: Long


) : Serializable