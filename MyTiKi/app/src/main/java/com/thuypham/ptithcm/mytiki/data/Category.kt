package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Category(
    var id: String? = null,
    var name: String? = null,
    var image: String? = null,
    var del: Boolean? = false
) : Parcelable, DynamicSearchAdapter.Searchable  {
    override fun getSearchCriteria(): String = name ?: ""
    override fun toString() = name ?: ""
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "image" to image
        )
    }
}