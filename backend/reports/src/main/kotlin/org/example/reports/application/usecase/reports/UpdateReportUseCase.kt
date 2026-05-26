package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.Photo
import org.example.reports.domain.model.Report
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.infrastructure.cloudinary.CloudinaryService
import org.example.reports.infrastructure.security.SpringSecurityUserProvider
import org.example.reports.presentation.dto.UpdateReportRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class UpdateReportUseCase(
    private val reportRepository: ReportRepository,
    private val photoRepository: PhotoRepository,
    private val cloudinaryService: CloudinaryService,
    private val authProvider: SpringSecurityUserProvider
) {
    @Transactional
    fun execute(id: Long, request: UpdateReportRequest, photo: MultipartFile?): Report {
        val currentEmail = authProvider.getCurrentUserEmail()

        val report = reportRepository.findById(id)
            ?: throw RuntimeException("Reporte no encontrado")

        if (report.user.email != currentEmail)
            throw RuntimeException("No tienes permiso para editar este reporte")

        val updated = report.copy(
            description         = request.description ?: report.description,
            approximateLocation = request.approximateLocation ?: report.approximateLocation,
            category            = request.category ?: report.category
        )
        val saved = reportRepository.save(updated)

        if (photo != null && !photo.isEmpty) {
            val newUrl = cloudinaryService.uploadPhoto(photo)
            photoRepository.findByReportId(id).forEach { photoRepository.deleteById(it.id) }
            photoRepository.save(
                Photo(
                    id          = 0,
                    url         = newUrl,
                    uploadDate  = LocalDateTime.now(),
                    aiValidated = false,
                    report      = saved
                )
            )
        }

        return saved
    }
}