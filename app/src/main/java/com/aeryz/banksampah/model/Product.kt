package com.aeryz.banksampah.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int? = null,
    val productName: String,
    val productType: String,
    val productImageUrl: String,
    val productDetail: String,
    val productPrice: Double,
    val productFormattedPrice: String,
) : Parcelable
