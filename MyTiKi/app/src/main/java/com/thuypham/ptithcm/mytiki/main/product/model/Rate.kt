package com.thuypham.ptithcm.mytiki.main.product.model

import com.google.gson.annotations.SerializedName

class Rate(
        @SerializedName("id")
        var id: String?,
        @SerializedName("rate_count")
        var rate_count: Long?,
        @SerializedName("time")
        var time: String?,
        @SerializedName("content")
        var content: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("id_acc")
        var id_acc: String?,
        @SerializedName("id_product")
        var id_product: String?
)