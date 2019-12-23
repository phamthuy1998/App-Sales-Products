package com.thuypham.ptithcm.mytiki.main.fragment.category.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Advertisement(

        @SerializedName("name")
        var name: String?,
        @SerializedName("id")
        var id: String?,
        @SerializedName("image")
        var image: String?,
        @SerializedName("id_category")
        var id_category: String,
        @SerializedName("name_category")
        var name_category: String
) : Serializable