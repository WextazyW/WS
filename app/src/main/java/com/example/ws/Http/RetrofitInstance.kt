package com.example.ws.Http

import com.example.ws.Http.Service.AuthApiService
import com.example.ws.Http.Service.NotificationApiService
import com.example.ws.Http.Service.OrderApiService
import com.example.ws.Http.Service.SneakerApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://172.20.10.4:5254/api/"

    val api: SneakerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SneakerApiService::class.java)
    }

    val authApi : AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    val notificationApi : NotificationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApiService::class.java)
    }

    val orderApi: OrderApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApiService::class.java)
    }
}