package org.example.reports.presentation.dto

import org.example.reports.domain.model.PriorityLevel
import java.time.LocalDateTime

data class ReportResponse(
    val id: Long,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val approximateLocation: String?,
    val reportDate: LocalDateTime,
    val category: String?,
    val status: String?,
    val userName: String,
    val userEmail: String,
    val userId: Long,
    val photoUrl: String?,
    val severity: Int,
    val affectedUsers: Int,
    val priorityLevel: PriorityLevel,
    val userHasVoted: Boolean
)