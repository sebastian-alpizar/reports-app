package org.example.reports.infrastructure.ai.dto

data class ReportAnalysisResponse(
    val valid: Boolean,
    val category: String,
    val severity: Int
)