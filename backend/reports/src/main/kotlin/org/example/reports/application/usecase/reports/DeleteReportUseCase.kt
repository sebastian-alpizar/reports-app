package org.example.reports.application.usecase.reports

import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.domain.repository.VoteRepository
import org.springframework.stereotype.Service

@Service
class DeleteReportUseCase(
    private val reportRepository: ReportRepository,
    private val photoRepository: PhotoRepository,
    private val voteRepository: VoteRepository
) {
    fun execute(id: Long) {

        // Primero elimina los votos asociados
        voteRepository.deleteByReportId(id)

        // Segundo elimina las fotos asociadas
        val photos = photoRepository.findByReportId(id)
        photos.forEach { photo ->
            photoRepository.deleteById(photo.id)
        }

        // Luego elimina el reporte
        reportRepository.deleteById(id)
    }
}