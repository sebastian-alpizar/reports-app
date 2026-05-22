package com.example.mobile.data.remote.dto

data class RegisterResponse(
    val message: String,
    val data: RegisterUserData
)

data class RegisterUserData(
    val id: Long,
    val name: String,
    val email: String,
    val isAdmin: Boolean
)