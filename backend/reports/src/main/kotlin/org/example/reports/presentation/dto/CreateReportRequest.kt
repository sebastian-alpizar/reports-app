package org.example.reports.presentation.dto

data class CreateReportRequest(
    val description: String,
    val latitude: Double?,
    val longitude: Double?,
    val approximateLocation: String?,
    val category: String?
)