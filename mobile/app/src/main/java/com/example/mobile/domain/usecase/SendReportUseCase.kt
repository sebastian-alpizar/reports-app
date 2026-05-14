package com.example.mobile.domain.usecase

import android.content.Context
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class SendReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(
        context: Context,
        report: Report
    ): Result<Report> {
        return repository.sendReport(context, report)
    }
}