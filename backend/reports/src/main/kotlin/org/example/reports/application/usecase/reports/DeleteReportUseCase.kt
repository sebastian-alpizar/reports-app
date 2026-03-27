package org.example.reports.application.usecase.reports

import org.example.reports.domain.repository.ReportRepository
import org.springframework.stereotype.Service

@Service
class DeleteReportUseCase(
    private val reportRepository: ReportRepository
) {

    fun execute(id: Long) {
        reportRepository.deleteById(id)
    }
}