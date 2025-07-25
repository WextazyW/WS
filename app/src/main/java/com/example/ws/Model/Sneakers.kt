package com.example.ws.Model

import kotlinx.serialization.Serializable

@Serializable
data class Sneakers(
    val id : Int = 0,
    val name : String,
    val price : Float,
    val description : String = "",
    val typeId : Int,
    val imageUrl: String = ""
)