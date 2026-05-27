package com.example.mobile.data.remote.api

import com.example.mobile.data.remote.dto.ApiResponseDto
import com.example.mobile.data.remote.dto.NotificationDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationApi {
    @GET("notifications/{userId}")
    suspend fun getNotifications(
        @Path("userId") userId: Long
    ): ApiResponseDto<List<NotificationDto>>

    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: String
    ): ApiResponseDto<Unit>
}