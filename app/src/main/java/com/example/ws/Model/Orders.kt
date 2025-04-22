package com.example.ws.Model

data class Orders(
    val id : Int,
    val userId: Int,
    val orderDate: String,
    val status: String,
    val deliveryAddress: String?,
    val totalAmount: Float
)