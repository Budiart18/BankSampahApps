package com.aeryz.banksampah.data.network.api.service

import com.aeryz.banksampah.BuildConfig
import com.aeryz.banksampah.data.network.api.model.category.CategoriesResponse
import com.aeryz.banksampah.data.network.api.model.order.OrderRequest
import com.aeryz.banksampah.data.network.api.model.order.OrderResponse
import com.aeryz.banksampah.data.network.api.model.product.ProductsResponse
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FoodGoApiService {

    @GET("listsampah")
    suspend fun getProducts(@Query("c") category: String? = null): ProductsResponse

    @GET("category")
    suspend fun getCategories(): CategoriesResponse

    @POST("order")
    suspend fun createOrder(@Body orderRequest: OrderRequest): OrderResponse
    companion object {
        @JvmStatic
        operator fun invoke(chucker: ChuckerInterceptor): FoodGoApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chucker)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(FoodGoApiService::class.java)
        }
    }
}
