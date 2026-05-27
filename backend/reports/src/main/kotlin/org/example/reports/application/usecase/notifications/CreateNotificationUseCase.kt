package org.example.reports.application.usecase.notifications

import org.example.reports.infrastructure.mongodb.document.NotificationDocument
import org.example.reports.infrastructure.mongodb.repository.NotificationMongoRepository
import org.springframework.stereotype.Service

@Service
class CreateNotificationUseCase(
    private val notificationRepository: NotificationMongoRepository
) {
    fun execute(
        userId: Long,
        title: String,
        message: String,
        reportId: Long
    ) {
        val notification = NotificationDocument(
            userId = userId,
            title = title,
            message = message,
            reportId = reportId
        )
        notificationRepository.save(notification)
    }
}