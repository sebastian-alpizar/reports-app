package com.example.mobile.domain.usecase

import com.example.mobile.domain.model.ReportStatus
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class UpdateReportStatusUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(id: Long, status: ReportStatus) {
        reportRepository.updateReportStatus(id, status).getOrThrow()
    }
}
