package com.example.ws.Http

import com.example.ws.Model.Users
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("User")
    suspend fun signUp(@Body user: Users): Users
    @POST("User/authenticate")
    suspend fun login(@Query("email") email: String, @Query("password") password: String): Users
}