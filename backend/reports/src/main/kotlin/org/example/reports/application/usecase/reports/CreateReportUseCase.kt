package org.example.reports.application.usecase.reports

import org.example.reports.application.usecase.notifications.CreateNotificationUseCase
import org.example.reports.domain.model.Photo
import org.example.reports.domain.model.Report
import org.example.reports.domain.model.ReportStatus
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.domain.repository.UserRepository
import org.example.reports.infrastructure.ai.GeminiService
import org.example.reports.infrastructure.cloudinary.CloudinaryService
import org.example.reports.presentation.dto.CreateReportRequest
import org.springframework.stereotype.Service
import org.example.reports.infrastructure.security.SpringSecurityUserProvider
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class CreateReportUseCase(
    private val reportRepository: ReportRepository,
    private val authProvider: SpringSecurityUserProvider,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository,
    private val cloudinaryService: CloudinaryService,
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val geminiService: GeminiService
) {
    @Transactional(rollbackFor = [Exception::class])
    fun execute(request: CreateReportRequest, photo: MultipartFile) {
        val email = authProvider.getCurrentUserEmail()

        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Usuario no encontrado")

//        val validImage = geminiService.validateImage(photo)
//
//        if (!validImage) {
//            throw RuntimeException("La imagen no es apta para la plataforma")
//        }

        val photoUrl = try {
            cloudinaryService.uploadPhoto(photo)
        } catch (e: Exception) {
            throw RuntimeException("Error al cargar la foto: ${e.message}", e)
        }
        val report = Report(
            id = 0,
            description = request.description,
            latitude = request.latitude,
            longitude = request.longitude,
            approximateLocation = request.approximateLocation,
            reportDate = LocalDateTime.now(),
            category = request.category,
            photoUrl = null,
            status = ReportStatus.PENDING,
            user = user
        )
        val savedReport = reportRepository.save(report)
        val admins = userRepository.findByIsAdminTrue()
        admins.forEach { admin ->
            createNotificationUseCase.execute(
                userId = admin.id,
                title = "Nuevo reporte creado",
                message = "Se creó un nuevo reporte: ${savedReport.description}",
                reportId = savedReport.id
            )
        }
        val photo = Photo(
            id = 0,
            url = photoUrl,
            uploadDate = LocalDateTime.now(),
            aiValidated = false,
            report = savedReport
        )
        photoRepository.save(photo)
    }
}