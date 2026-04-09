package com.example.mobile.data.remote.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val nationalId: String,
    val password: String
)