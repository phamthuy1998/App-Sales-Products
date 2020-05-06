package com.thuypham.ptithcm.mytiki.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Category(
        @SerializedName("id")
        var idCategory: String?,
        @SerializedName("name")
        var nameCategory: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("category_count")
        var categoryCount: Long?
): Serializable