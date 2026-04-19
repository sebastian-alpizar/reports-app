package com.example.mobile.domain.repository

import android.content.Context
import com.example.mobile.domain.model.Report

interface ReportRepository {
    suspend fun sendReport(
        context: Context,
        report: Report
    ): Result<Unit>
}