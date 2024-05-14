package com.aeryz.banksampah.data.repository

import com.aeryz.banksampah.data.local.database.datasource.CartDataSource
import com.aeryz.banksampah.data.local.database.entity.CartEntity
import com.aeryz.banksampah.data.local.database.mapper.toCartEntity
import com.aeryz.banksampah.data.local.database.mapper.toCartList
import com.aeryz.banksampah.data.network.api.datasource.FoodGoDataSource
import com.aeryz.banksampah.data.network.api.model.order.OrderRequest
import com.aeryz.banksampah.model.Cart
import com.aeryz.banksampah.model.Product
import com.aeryz.banksampah.model.toOrderItemRequestList
import com.aeryz.banksampah.utils.ResultWrapper
import com.aeryz.banksampah.utils.proceed
import com.aeryz.banksampah.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface CartRepository {
    fun getUserCartData(): Flow<ResultWrapper<Pair<List<Cart>, Double>>>
    suspend fun createCart(product: Product, totalQuantity: Int, itemNotes: String): Flow<ResultWrapper<Boolean>>
    suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun setCartNotes(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteAllCarts()
    suspend fun createOrder(items: List<Cart>, totalPrice: Int, username: String): Flow<ResultWrapper<Boolean>>
}

class CartRepositoryImpl(
    private val dataSource: CartDataSource,
    private val apiDataSource: FoodGoDataSource
) : CartRepository {

    override fun getUserCartData(): Flow<ResultWrapper<Pair<List<Cart>, Double>>> {
        return dataSource.getAllCarts()
            .map {
                proceed {
                    val result = it.toCartList()
                    val totalPrice = result.sumOf {
                        val pricePerItem = it.productPrice
                        val quantity = it.itemQuantity
                        pricePerItem * quantity
                    }
                    Pair(result, totalPrice)
                }
            }.map {
                if (it.payload?.first?.isEmpty() == true) {
                    ResultWrapper.Empty(it.payload)
                } else {
                    it
                }
            }.catch {
                emit(ResultWrapper.Error(exception = Exception(it)))
            }.onStart {
                emit(ResultWrapper.Loading())
                delay(2000)
            }
    }

    override suspend fun createCart(
        product: Product,
        totalQuantity: Int,
        itemNotes: String
    ): Flow<ResultWrapper<Boolean>> {
        return product.id?.let { productId ->
            proceedFlow {
                val affectedRow = dataSource.insertCart(
                    CartEntity(
                        productId = productId,
                        itemQuantity = totalQuantity,
                        itemNotes = itemNotes,
                        productName = product.productName,
                        productPrice = product.productPrice,
                        productImgUrl = product.productImageUrl
                    )
                )
                affectedRow > 0
            }
        } ?: flow {
            emit(ResultWrapper.Error(IllegalStateException("Product ID not found")))
        }
    }

    override suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            itemQuantity += 1
        }
        return proceedFlow { dataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
    }

    override suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            itemQuantity -= 1
        }
        return if (modifiedCart.itemQuantity <= 0) {
            proceedFlow { dataSource.deleteCart(modifiedCart.toCartEntity()) > 0 }
        } else {
            proceedFlow { dataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
        }
    }

    override suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.deleteCart(item.toCartEntity()) > 0 }
    }

    override suspend fun setCartNotes(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { dataSource.updateCart(item.toCartEntity()) > 0 }
    }

    override suspend fun deleteAllCarts() {
        dataSource.deleteAllCarts()
    }

    override suspend fun createOrder(items: List<Cart>, totalPrice: Int, username: String): Flow<ResultWrapper<Boolean>> {
        return proceedFlow {
            val orderItem = items.toOrderItemRequestList()
            val orderRequest = OrderRequest(orderItem, totalPrice, username)
            apiDataSource.createOrder(orderRequest).status == true
        }
    }
}
