package com.example.mobile.data.remote.api

import com.example.mobile.data.remote.dto.LoginRequest
import com.example.mobile.data.remote.dto.LoginResponse
import com.example.mobile.data.remote.dto.RegisterRequest
import com.example.mobile.data.remote.dto.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("users")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
}