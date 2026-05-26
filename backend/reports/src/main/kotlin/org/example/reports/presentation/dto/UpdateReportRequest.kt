package org.example.reports.presentation.dto

data class UpdateReportRequest(
    val description: String? = null,
    val approximateLocation: String? = null,
    val category: String? = null
)