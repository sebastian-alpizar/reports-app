package org.example.reports.application.usecase.notifications

import org.example.reports.infrastructure.mongodb.repository.NotificationMongoRepository
import org.springframework.stereotype.Service

@Service
class DeleteNotificationUseCase(
    private val notificationRepository: NotificationMongoRepository
) {
    fun execute(notificationId: String) {
        notificationRepository.deleteById(
            notificationId
        )
    }
}