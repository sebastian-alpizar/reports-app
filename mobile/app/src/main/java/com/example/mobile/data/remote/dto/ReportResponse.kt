package com.example.mobile.data.remote.dto

import com.example.mobile.domain.model.PriorityLevel

data class ReportResponse(
    val id: Long,
    val description: String,
    val approximateLocation: String?,
    val latitude: Double?,
    val longitude: Double?,
    val category: String?,
    val status: String?,
    val userName: String?,
    val userEmail: String?,
    val userId: Long?,
    val photoUrl: String?,
    val reportDate: String?,
    val severity: Int,
    val affectedUsers: Int,
    val priorityLevel: PriorityLevel,
    val userHasVoted: Boolean
)
