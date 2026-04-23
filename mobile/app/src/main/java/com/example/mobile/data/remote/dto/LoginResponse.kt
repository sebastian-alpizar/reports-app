package com.example.mobile.data.remote.dto

data class LoginResponse(
    val message: String,
    val data: LoginData
)

data class LoginData(
    val token: String
)