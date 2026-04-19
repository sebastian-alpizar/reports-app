package com.example.mobile.data.remote.dto

data class ReportRequest(
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val approximateLocation: String?,
    val category: String?
)