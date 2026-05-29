package com.example.mobile.data.remote.dto

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val nationalId: String? = null
)