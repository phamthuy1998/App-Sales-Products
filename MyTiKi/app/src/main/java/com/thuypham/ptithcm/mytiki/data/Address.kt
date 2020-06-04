package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable





data class Address(
    var id: String?=null,
    var name: String?=null,
    var phone: String?=null,
    var address: String?=null,
    var default: Boolean?=null
) : Serializable
