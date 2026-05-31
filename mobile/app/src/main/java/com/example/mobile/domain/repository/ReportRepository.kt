package com.example.mobile.domain.repository

import android.content.Context
import com.example.mobile.data.remote.dto.CreateReportDto
import com.example.mobile.data.remote.dto.StatisticsResponse
import com.example.mobile.data.remote.dto.UpdateStatusRequest
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.model.ReportStatus

interface ReportRepository {
    suspend fun sendReport(
        context: Context,
        report: CreateReportDto
    ): Result<Unit>

    suspend fun getAllReports(): Result<List<Report>>

    suspend fun updateReportStatus(
        id: Long,
        status: UpdateStatusRequest
    ): Result<String>

    suspend fun getNearbyReports(
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double = 5.0
    ): Result<List<Report>>

    suspend fun updateReport(
        context: Context,
        reportId: Long,
        description: String,
        imageUri: String?
    ): Result<Unit>

    suspend fun deleteReport(reportId: Long): Result<Unit>

    suspend fun voteReport(reportId: Long): Result<String>
    
    suspend fun getReportsByUser(userId: Long): Result<List<Report>>
    suspend fun getStatistics(): Result<StatisticsResponse>
}