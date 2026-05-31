package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.PriorityLevel
import org.example.reports.domain.model.Report
import org.example.reports.domain.model.ReportStatus
import java.time.Duration
import java.time.LocalDateTime

object PriorityCalculator {

    fun calculate(report: Report, affectedUsers: Int): Double {

        val severityWeight = report.severity * 40.0

        val affectedUsersWeight = affectedUsers * 5.0

        val pendingDays = if (report.status != ReportStatus.APPROVED) {
            Duration.between(report.reportDate, LocalDateTime.now()).toDays()
        } else {
            0
        }

        val timeWeight = pendingDays * 10

        return severityWeight +
                affectedUsersWeight +
                timeWeight
    }

    fun getLabel(report: Report, affectedUsers: Int): PriorityLevel {
        val score = calculate(report, affectedUsers)

        return when {
            score >= 250 -> PriorityLevel.CRITICAL
            score >= 150 -> PriorityLevel.HIGH
            score >= 80 -> PriorityLevel.MEDIUM
            else -> PriorityLevel.LOW
        }
    }
}