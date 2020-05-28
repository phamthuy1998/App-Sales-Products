package com.thuypham.ptithcm.mytiki.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.thuypham.ptithcm.mytiki.base.DynamicSearchAdapter
import java.io.Serializable

@IgnoreExtraProperties
data class Order(
    var id: String? = null,
    var name: String? = null,
    var id_user: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var date: String? = null,
    var dateSearch: String? = null,
    var price: Long? = null,
    var status: Int? = null
) : Serializable , DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String = "$name $phone $address $date $price"
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "id_user" to id_user,
            "phone" to phone,
            "address" to address,
            "date" to date,
            "price" to price,
            "status" to status
        )
    }
}

data class OrderStatus(
    var id: Int? = null,
    var statusName: String? = null
)