package com.example.mobile.domain.repository

import android.content.Context
import com.example.mobile.domain.model.Report

interface ReportRepository {
    suspend fun sendReport(
        context: Context,
        report: Report
    ): Result<Report>

    suspend fun getNearbyReports(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Result<List<Report>>

    suspend fun getMyReports(): Result<List<Report>>
}