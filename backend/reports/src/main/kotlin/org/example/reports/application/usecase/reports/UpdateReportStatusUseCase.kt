package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.ReportStatus
import org.example.reports.domain.repository.ReportRepository
import org.springframework.stereotype.Service

@Service
class UpdateReportStatusUseCase(
    private val reportRepository: ReportRepository
) {

    fun execute(reportId: Long, status: String) {
        val reportStatus = when (status.uppercase()) {
            "PENDIENTE"   -> ReportStatus.PENDING
            "IN_PROGRESS" -> ReportStatus.REJECTED
            "RESOLVED"    -> ReportStatus.APPROVED
            else          -> ReportStatus.PENDING
        }
        reportRepository.updateStatus(reportId = reportId, status = reportStatus)
    }
}