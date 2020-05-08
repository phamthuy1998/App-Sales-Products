package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class ProductCartDetail (
    var id: String? = null,
    var name: String? = null,
    var price: Long? = null,
    var number_product: Long? = null,
    var image: String? = null,
    var product_count: Long? = null,
    var id_category: String? = null,
    var sale: Long? = null
) :  Parcelable