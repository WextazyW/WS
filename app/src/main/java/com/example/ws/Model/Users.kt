package com.example.ws.Model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val id : Int? = 0,
    val Name : String,
    val Email : String,
    val id_uuid: String?
)
