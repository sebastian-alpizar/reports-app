package com.example.mobile.domain.usecase

import com.example.mobile.domain.model.Notification
import com.example.mobile.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(userId: Long):
            Result<List<Notification>> {
        return repository.getNotifications(userId)
    }
}