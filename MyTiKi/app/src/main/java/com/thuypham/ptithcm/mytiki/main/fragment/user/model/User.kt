package com.thuypham.ptithcm.mytiki.main.fragment.user.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class User(
    @SerializedName("name")
    var name: String?,
    @SerializedName("phone")
    var phone: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("day_of_birth")
    var dayofbirth: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("day_create_acc")
    var dayCreateAcc: String?
):Serializable