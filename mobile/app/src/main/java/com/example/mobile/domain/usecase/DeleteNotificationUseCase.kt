package com.example.mobile.domain.usecase

import com.example.mobile.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String):
            Result<String> {
        return repository.deleteNotification(notificationId)
    }
}