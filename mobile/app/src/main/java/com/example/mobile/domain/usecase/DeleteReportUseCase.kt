package com.example.mobile.domain.usecase

import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class DeleteReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(reportId: String): Result<Unit> {
        return repository.deleteReport(reportId)
    }
}