package com.example.ws.Http.Service

import com.example.ws.Model.Users
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {
    @POST("User")
    suspend fun signUp(@Body user: Users): Users
    @POST("User/authenticate")
    suspend fun login(@Query("email") email: String, @Query("password") password: String): Users
    @GET("User/{id}")
    suspend fun getUserById(@Path("id") id: Int): Users
    @PUT("User/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: Users): Users
}