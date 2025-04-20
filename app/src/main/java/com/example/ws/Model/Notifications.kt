package com.example.ws.Model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    @SerialName("id") val id: Int? = 0,
    @SerialName("header") val header: String = "",
    @SerialName("body") val body: String = "",
    @SerialName("date") val date: String = "",
    @SerialName("userId") val userId : Int? = 0
)
