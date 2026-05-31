package org.example.reports.domain.model

import java.time.LocalDateTime

data class Vote(
    val id: Long,
    val report: Report,
    val user: User,
    val voteDate: LocalDateTime
)