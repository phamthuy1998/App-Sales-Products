package com.thuypham.ptithcm.mytiki.main.fragment.user.cart.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductCartDetail (

    @SerializedName("id")
    var id: String?,

    @SerializedName("name")
    var name: String?,

    @SerializedName("price")
    var price: Long?,

    var number_product: Long?,


    @SerializedName("image")
    var image: String?,

    @SerializedName("product_count")
    var product_count: Long,

    @SerializedName("id_category")
    var id_category: String?,

    @SerializedName("sale")
    var sale: Long


) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeValue(price)
        parcel.writeValue(number_product)
        parcel.writeString(image)
        parcel.writeLong(product_count)
        parcel.writeString(id_category)
        parcel.writeLong(sale)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductCartDetail> {
        override fun createFromParcel(parcel: Parcel): ProductCartDetail {
            return ProductCartDetail(
                parcel
            )
        }

        override fun newArray(size: Int): Array<ProductCartDetail?> {
            return arrayOfNulls(size)
        }
    }
}