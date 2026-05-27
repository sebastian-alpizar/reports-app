package org.example.reports.application.usecase.notifications

import org.example.reports.infrastructure.mongodb.repository.NotificationMongoRepository
import org.example.reports.presentation.dto.NotificationResponse
import org.springframework.stereotype.Service

@Service
class GetNotificationsUseCase(
    private val notificationRepository: NotificationMongoRepository
) {

    fun execute(userId: Long): List<NotificationResponse> {
        return notificationRepository
            .findByUserIdOrderByCreatedAtDesc(userId)
            .map {
                NotificationResponse(
                    id = it.id,
                    title = it.title,
                    message = it.message,
                    reportId = it.reportId,
                    read = it.read,
                    createdAt = it.createdAt
                )
            }
    }
}