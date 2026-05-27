package com.example.mobile.domain.repository

import com.example.mobile.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(userId: Long):
            Result<List<Notification>>

    suspend fun deleteNotification(notificationId: String):
            Result<String>
}