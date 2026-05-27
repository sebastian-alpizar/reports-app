package org.example.reports.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: String? = null,
    val userId: Long,
    val title: String,
    val message: String,
    val reportId: Long,
    val read: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)