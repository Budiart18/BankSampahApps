package com.aeryz.banksampah.model

import com.aeryz.banksampah.data.network.api.model.order.OrderItemRequest

data class Cart(
    var id: Int? = null,
    var productId: Int,
    val productName: String,
    val productPrice: Double,
    val productImgUrl: String,
    var itemQuantity: Int = 0,
    var itemNotes: String? = null
)

fun Cart.toOrderItemRequest() = OrderItemRequest(
    notes = this.itemNotes.orEmpty(),
    price = this.productPrice.toInt(),
    name = this.productName,
    qty = this.itemQuantity
)

fun Collection<Cart>.toOrderItemRequestList() = this.map { it.toOrderItemRequest() }
