package com.thuypham.ptithcm.mytiki.main.product.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Comment (

    @SerializedName("id")
    var comment_id: String?,

    @SerializedName("date_cmt")
    var date_cmt: String?,

    @SerializedName("user_cmt")
    var user_cmt: String?,

    @SerializedName("content")
    var content: String?,

    @SerializedName("image_cmt")
    var image_cmt: String?,

    @SerializedName("product_id")
    var product_id: Long,

    @SerializedName("star")
    var star: Long = 0

) : Serializable