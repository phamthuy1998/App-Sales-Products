package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Product(
    var id: String?=null,
    var name: String? = null,
    var price: Long? = null,
    var image: String? = null,
    var infor: String? = null,
    var product_count: Long? = null,
    var id_category: String? = null,
    var sale: Long

) : Serializable, Parcelable