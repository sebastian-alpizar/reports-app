package com.example.mobile.data.remote.dto

data class ReportResponse(
    val id: Long,
    val description: String,
    val approximateLocation: String?,
    val latitude: Double?,
    val longitude: Double?,
    val reportDate: String,
    val category: String?,
    val status: String?,
    val userName: String?,
    val userEmail: String?,
    val userId: Long?,
    val photoUrl: String?
)