package org.example.reports.application.usecase.photos

import org.example.reports.domain.model.Photo
import org.example.reports.domain.repository.PhotoRepository
import org.springframework.stereotype.Service

@Service
class GetPhotosByReportUseCase(
    private val photoRepository: PhotoRepository
) {

    fun execute(reportId: Long): List<Photo> {
        return photoRepository.findByReportId(reportId)
    }
}