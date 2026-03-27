package org.example.reports.domain.model

import java.time.LocalDateTime

data class Report(
    val id: Long,
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val approximateLocation: String?,
    val reportDate: LocalDateTime,
    val category: String?,
    val status: ReportStatus,
    val user: User
)