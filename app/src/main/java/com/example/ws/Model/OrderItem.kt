package com.example.ws.Model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Int,
    val orderId: Int,
    val sneakerId: Int,
    val quantity: Int
)