package com.thuypham.ptithcm.mytiki.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class OrderDetail(
    var id: String? = null,
    var product_name: String? = null,
    var id_product: String? = null,
    var image_product: String? = null,
    var product_count: Long? = null,
    var product_price: Long? = null,
    var id_order: String? = null
) : Serializable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "product_name" to product_name,
            "id_product" to id_product,
            "image_product" to image_product,
            "product_count" to product_count,
            "product_price" to product_price,
            "id_order" to id_order
        )
    }
}


