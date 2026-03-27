package org.example.reports.presentation.dto

import java.time.LocalDateTime

data class PhotoResponse(
    val id: Long,
    val url: String,
    val uploadDate: LocalDateTime,
    val aiValidated: Boolean,
    val reportId: Long
)