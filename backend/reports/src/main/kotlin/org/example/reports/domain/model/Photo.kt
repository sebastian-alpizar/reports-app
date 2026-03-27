package org.example.reports.domain.model

import java.time.LocalDateTime

data class Photo(
    val id: Long,
    val url: String,
    val uploadDate: LocalDateTime,
    val aiValidated: Boolean,
    val report: Report
)