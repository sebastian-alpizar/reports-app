package com.example.mobile.data.remote.api

import com.example.mobile.data.remote.dto.UpdateUserRequest
import com.example.mobile.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest
    ): UserDto
}