package org.example.reports.domain.repository

import org.example.reports.domain.model.Vote

interface VoteRepository {
    fun save(vote: Vote): Vote
    fun existsByReportIdAndUserId(
        reportId: Long,
        userId: Long
    ): Boolean
    fun countByReportId(
        reportId: Long
    ): Int
    fun deleteByReportId(reportId: Long)
}