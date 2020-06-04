package com.thuypham.ptithcm.mytiki.data

import com.google.gson.annotations.SerializedName

data class ResultStory(
    @SerializedName("")
    var by: String,
    var descendants: Int,
    var id: Int,
    var kids: List<Int>,
    var score: Int,
    var time: Int,
    var title: String,
    var type: String,
    var url: String
)