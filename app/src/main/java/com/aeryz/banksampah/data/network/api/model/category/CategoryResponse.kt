package com.aeryz.banksampah.data.network.api.model.category

import androidx.annotation.Keep
import com.aeryz.banksampah.model.Category
import com.google.gson.annotations.SerializedName

@Keep
data class CategoryResponse(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("nama")
    val name: String?
)

fun CategoryResponse.toCategory() = Category(
    id = this.id ?: 0,
    categoryName = this.name.orEmpty(),
    categoryImage = this.imageUrl.orEmpty()
)

fun Collection<CategoryResponse>.toCategoryList() = this.map {
    it.toCategory()
}
