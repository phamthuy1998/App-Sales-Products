package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class User(
    var id: String?= null,
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var password: String? = null,
    var birthday: String? = null,
    var gender: String? = null,
    var daycreate: String? = null,
    var role: Long? = 1,
    var active: Boolean? = null,
    var del: Boolean? = null
): Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "email" to email,
            "password" to password,
            "birthday" to birthday,
            "gender" to gender,
            "daycreate" to daycreate,
            "role" to role,
            "active" to active,
            "del" to del
        )
    }
}