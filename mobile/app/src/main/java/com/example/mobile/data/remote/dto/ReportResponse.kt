package com.example.mobile.data.remote.dto

data class ReportResponse(
    val id: Long,
    val description: String,
    val approximateLocation: String?,
    val reportDate: String,
    val status: String?,
    val userName: String?,
    val userEmail: String?
)
