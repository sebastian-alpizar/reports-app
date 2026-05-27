package org.example.reports.infrastructure.mongodb.document

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "notifications")
data class NotificationDocument(

    @Id
    val id: String? = null,

    val userId: Long,

    val title: String,

    val message: String,

    val reportId: Long,

    val read: Boolean = false,

    val createdAt: LocalDateTime = LocalDateTime.now()
)