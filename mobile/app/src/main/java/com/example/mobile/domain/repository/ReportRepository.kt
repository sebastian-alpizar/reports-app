package com.example.mobile.domain.repository

import android.content.Context
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.model.ReportStatus

interface ReportRepository {
    suspend fun sendReport(
        context: Context,
        report: Report
    ): Result<Unit>

    suspend fun getAllReports(): Result<List<ReportResponse>>

    suspend fun updateReportStatus(
        id: Long,
        status: ReportStatus
    ): Result<Unit>

    suspend fun getNearbyReports(
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double = 5.0
    ): Result<List<Report>>

    suspend fun updateReport(
        context: Context,
        reportId: String,
        description: String,
        imageUri: String?
    ): Result<Unit>

}