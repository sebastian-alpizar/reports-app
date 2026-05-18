package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.Report
import org.example.reports.domain.repository.ReportRepository
import org.springframework.stereotype.Service

@Service
class GetReportsUseCase(
    private val reportRepository: ReportRepository,
) {
    fun getAllReports(): List<Report> {
        return reportRepository.findAll()
    }

    fun getReportById(id: Long): Report {
        return reportRepository.findById(id)
            ?: throw RuntimeException("Reporte no encontrado")
    }

    fun getReportsByUser(userId: Long): List<Report> {
        return reportRepository.findByUserId(userId)
    }
}