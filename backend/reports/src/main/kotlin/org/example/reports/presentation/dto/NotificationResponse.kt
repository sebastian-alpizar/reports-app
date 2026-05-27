package org.example.reports.presentation.dto

import java.time.LocalDateTime

data class NotificationResponse(
    val id: String?,
    val title: String,
    val message: String,
    val reportId: Long,
    val read: Boolean,
    val createdAt: LocalDateTime
)