package org.example.reports.application.usecase.reports

import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.springframework.stereotype.Service

@Service
class DeleteReportUseCase(
    private val reportRepository: ReportRepository,
    private val photoRepository: PhotoRepository
) {
    fun execute(id: Long) {
        // Primero elimina las fotos asociadas
        val photos = photoRepository.findByReportId(id)
        photos.forEach { photo ->
            photoRepository.deleteById(photo.id)
        }

        // Luego elimina el reporte
        reportRepository.deleteById(id)
    }
}