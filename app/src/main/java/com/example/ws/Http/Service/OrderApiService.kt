package com.example.ws.Http.Service

import com.example.ws.Model.OrderItem
import com.example.ws.Model.Orders
import com.example.ws.Model.Sneakers
import io.github.jan.supabase.postgrest.query.Order
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {
    @GET("Order")
    suspend fun getOrders(): List<Orders>

    @GET("Orderitem/byOrder/{orderId}")
    suspend fun getOrderItemsByOrderId(@Path("orderId") orderId: Int): List<OrderItem>

    @GET("Sneaker/{id}")
    suspend fun getSneakerById(@Path("id") id: Int): Sneakers

    @POST("Order")
    suspend fun createOrder(@Body order: Orders): Orders

    @POST("Orderitem")
    suspend fun createOrderItem(@Body orderItem: OrderItem): OrderItem
}