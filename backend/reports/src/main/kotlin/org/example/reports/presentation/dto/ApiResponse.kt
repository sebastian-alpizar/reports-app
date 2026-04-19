package org.example.reports.presentation.dto

data class ApiResponse<T>(
    val message: String,
    val data: T? = null
)