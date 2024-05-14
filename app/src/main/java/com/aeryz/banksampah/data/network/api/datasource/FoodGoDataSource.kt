package com.aeryz.banksampah.data.network.api.datasource

import com.aeryz.banksampah.data.network.api.model.category.CategoriesResponse
import com.aeryz.banksampah.data.network.api.model.order.OrderRequest
import com.aeryz.banksampah.data.network.api.model.order.OrderResponse
import com.aeryz.banksampah.data.network.api.model.product.ProductsResponse
import com.aeryz.banksampah.data.network.api.service.FoodGoApiService

interface FoodGoDataSource {
    suspend fun getProducts(category: String? = null): ProductsResponse
    suspend fun getCategories(): CategoriesResponse
    suspend fun createOrder(orderRequest: OrderRequest): OrderResponse
}

class FoodGoApiDataSource(private val service: FoodGoApiService) : FoodGoDataSource {

    override suspend fun getProducts(category: String?): ProductsResponse {
        return service.getProducts(category)
    }

    override suspend fun getCategories(): CategoriesResponse {
        return service.getCategories()
    }

    override suspend fun createOrder(orderRequest: OrderRequest): OrderResponse {
        return service.createOrder(orderRequest)
    }
}
