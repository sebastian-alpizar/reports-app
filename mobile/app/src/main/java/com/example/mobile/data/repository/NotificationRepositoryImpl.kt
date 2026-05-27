package com.example.mobile.data.repository

import com.example.mobile.data.remote.api.NotificationApi
import com.example.mobile.data.remote.dto.NotificationDto
import com.example.mobile.data.remote.util.ErrorParser
import com.example.mobile.domain.model.Notification
import com.example.mobile.domain.repository.NotificationRepository
import retrofit2.HttpException
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepository {

    override suspend fun getNotifications(userId: Long): Result<List<Notification>> {
        return try {
            val result = api.getNotifications(userId)
            val notifications = result.data.orEmpty().map {
                it.toDomain()
            }
            Result.success(notifications)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    override suspend fun deleteNotification(notificationId: String): Result<String> {
        return try {
            val result = api.deleteNotification(notificationId)
            Result.success(result.message)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    fun NotificationDto.toDomain(): Notification {
        return Notification(
            id = id,
            title = title,
            message = message,
            reportId = reportId,
            read = read,
            createdAt = createdAt
        )
    }
}