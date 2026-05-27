package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.Report
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.springframework.stereotype.Service

@Service
class GetReportsUseCase(
    private val reportRepository: ReportRepository,
    private val photoRepository: PhotoRepository
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

    fun getNearbyReports(lat: Double, lng: Double, radiusKm: Double = 5.0): List<Report> {
        return reportRepository.findNearby(lat, lng, radiusKm).map { it.withPhoto() }
    }

    private fun Report.withPhoto(): Report {
        val photo = photoRepository.findByReportId(this.id).firstOrNull()
        return this.copy(photoUrl = photo?.url)
    }
}