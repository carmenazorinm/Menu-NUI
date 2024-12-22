package com.example.npi_app

import android.os.Parcel
import android.os.Parcelable

data class RedeemItem(
    val id: String,
    val title: String,
    val description: String,
    val cost: Int,
    val category: String,
    val type: String,           // Obligatorio
    val discount: Double = 0.0,      // Opcional, por defecto 0
    val image: String? = null   // Opcional, por defecto nulo
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(cost)
        parcel.writeString(category)
        parcel.writeString(type)
        parcel.writeDouble(discount)
        parcel.writeString(image)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<RedeemItem> {
        override fun createFromParcel(parcel: Parcel): RedeemItem {
            return RedeemItem(parcel)
        }

        override fun newArray(size: Int): Array<RedeemItem?> {
            return arrayOfNulls(size)
        }
    }
}
