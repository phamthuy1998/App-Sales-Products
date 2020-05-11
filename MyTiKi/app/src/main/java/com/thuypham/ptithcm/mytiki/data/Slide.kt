package com.thuypham.ptithcm.mytiki.data

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Slide(
    var name: String?=null,
    var id: String?=null,
    var image: String?=null,
    var id_category: String?=null,
    var name_category: String?=null
) :  Parcelable, DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String = "$name $name_category"
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "image" to image,
            "id_category" to id_category,
            "name_category" to name_category
        )
    }
}