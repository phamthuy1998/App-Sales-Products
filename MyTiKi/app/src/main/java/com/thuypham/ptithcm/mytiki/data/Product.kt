package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
@IgnoreExtraProperties
data class Product(
    var id: String? = null,
    var name: String? = null,
    var price: Long? = null,
    var image: String? = null,
    var infor: String? = null,
    var product_count: Long? = null,
    var id_category: String? = null,
    var sale: Long = 0,
    var del: Boolean? = false
) : Serializable, Parcelable, DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String = name ?: ""
    override fun toString() = name ?: ""
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "price" to price,
            "image" to image,
            "infor" to infor,
            "product_count" to product_count,
            "id_category" to id_category,
            "sale" to sale,
            "del" to del
        )
    }
}