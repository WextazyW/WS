package com.example.ws.Http.Service

import com.example.ws.Model.Sneakers
import retrofit2.http.GET

interface SneakerApiService {
    @GET("Sneaker")
    suspend fun getSneakers() : List<Sneakers>
}