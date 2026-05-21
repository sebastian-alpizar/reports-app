package com.example.mobile.data.remote.dto

data class ReportResponse(
    val id: Long,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val approximateLocation: String?,
    val reportDate: String?,
    val category: String?,
    val status: String?,
    val userId: Long?
)
