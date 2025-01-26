package com.example.ws.Model

import kotlinx.serialization.Serializable

@Serializable
data class Sneakers(
    val id : Int = 0,
    val Name : String,
    val Price : Float,
    val Description : String
)
