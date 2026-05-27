package org.example.reports.infrastructure.mongodb.repository

import org.example.reports.infrastructure.mongodb.document.NotificationDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationMongoRepository : MongoRepository<NotificationDocument, String> {
    fun findByUserIdOrderByCreatedAtDesc(
        userId: Long
    ): List<NotificationDocument>
}

