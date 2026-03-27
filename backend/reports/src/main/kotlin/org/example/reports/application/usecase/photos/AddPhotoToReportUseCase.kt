package org.example.reports.application.usecase.photos

import org.example.reports.application.usecase.reports.GetReportsUseCase
import org.example.reports.domain.model.Photo
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.presentation.dto.CreatePhotoRequest
import org.springframework.stereotype.Service

@Service
class AddPhotoToReportUseCase(
    private val photoRepository: PhotoRepository,
    private val reportQueryService: GetReportsUseCase,
) {

    fun execute(request: CreatePhotoRequest): Photo {
        val report = reportQueryService.getReportById(request.reportId)

        val photo = Photo(
            id = 0,
            url = request.url,
            uploadDate = java.time.LocalDateTime.now(),
            aiValidated = false,
            report = report
        )

        return photoRepository.save(photo)
    }
}