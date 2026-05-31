package com.example.mobile.data.remote.dto

data class StatisticsResponse(
    val totalReports: Int,
    val reportsThisWeek: Int,
    val reportsThisMonth: Int,
    val mostReportedCategory: String?,
    val averageVotesPerReport: Double,
    val topPriorityReports: Int,
    val resolvedPercentage: Double
)