package com.example.mobile.data.remote.dto

data class ApiResponseDto<T>(
    val message: String,
    val data: T? = null
)