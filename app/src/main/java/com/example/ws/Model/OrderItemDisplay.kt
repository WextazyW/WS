package com.example.ws.Model

data class OrderItemDisplay(
    val quantity: Int,
    val imageUrl: String,
    val name : String? = "",
    val price : Double? = 0.0,
) : java.io.Serializable