package com.aeryz.banksampah.data.network.api.model.product

import androidx.annotation.Keep
import com.aeryz.banksampah.model.Product
import com.google.gson.annotations.SerializedName

@Keep
data class ProductItemResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("nama")
    val name: String?,
    @SerializedName("jenis")
    val type: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("detail")
    val detail: String?,
    @SerializedName("harga")
    val price: Double?,
    @SerializedName("format_harga")
    val formattedPrice: String?
)

fun ProductItemResponse.toProduct() = Product(
    id = this.id ?: 0,
    productName = this.name.orEmpty(),
    productType = this.type.orEmpty(),
    productImageUrl = this.imageUrl.orEmpty(),
    productDetail = this.detail.orEmpty(),
    productPrice = this.price ?: 0.0,
    productFormattedPrice = this.formattedPrice.orEmpty()
)

fun Collection<ProductItemResponse>.toProductList() = this.map { it.toProduct() }
