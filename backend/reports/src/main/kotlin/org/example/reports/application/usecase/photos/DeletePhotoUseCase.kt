package org.example.reports.application.usecase.photos

import org.example.reports.domain.repository.PhotoRepository
import org.springframework.stereotype.Service

@Service
class DeletePhotoUseCase(
    private val photoRepository: PhotoRepository
) {

    fun execute(id: Long) {
        photoRepository.deleteById(id)
    }
}