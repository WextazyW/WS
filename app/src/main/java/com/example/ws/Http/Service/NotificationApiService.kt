package com.example.ws.Http.Service

import com.example.ws.Model.Notification
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApiService {
    @GET("Notification/user/{userId}")
    suspend fun getNotificationsByUserId(@Path("userId") userId: Int): List<Notification>
    @POST("Notification")
    suspend fun createNotification(@Body notification: Notification): Notification
}