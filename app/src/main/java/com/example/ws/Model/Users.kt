package com.example.ws.Model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val id : Int? = 0,
    val name : String = "",
    val email : String = "",
    val password : String = "",
)
