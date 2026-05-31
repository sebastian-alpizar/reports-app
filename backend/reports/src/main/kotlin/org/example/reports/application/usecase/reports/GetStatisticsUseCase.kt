package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.PriorityLevel
import org.example.reports.domain.model.ReportStatus
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.domain.repository.VoteRepository
import org.example.reports.presentation.dto.StatisticsResponse
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GetStatisticsUseCase(
    private val reportRepository: ReportRepository,
    private val voteRepository: VoteRepository
) {

    fun execute(): StatisticsResponse {

        val reports = reportRepository.findAll()

        val now = LocalDateTime.now()
        val weekAgo = now.minusWeeks(1)
        val monthAgo = now.minusMonths(1)

        val totalReports = reports.size

        val reportsThisWeek = reports.count {
            it.reportDate.isAfter(weekAgo)
        }

        val reportsThisMonth = reports.count {
            it.reportDate.isAfter(monthAgo)
        }

        val mostReportedCategory = reports
            .groupBy { it.category ?: "Sin categoría" }
            .maxByOrNull { it.value.size }
            ?.key

        val totalVotes = reports.sumOf {
            voteRepository.countByReportId(it.id)
        }

        val averageVotesPerReport =
            if (totalReports > 0)
                totalVotes.toDouble() / totalReports
            else
                0.0

//        val topPriorityReports = reports.count {
//            it.severity >= 4
//        }

        val topPriorityReports = reports.count { report ->
            val votes = voteRepository.countByReportId(report.id)

            PriorityCalculator.getLabel(report, votes) ==
                    PriorityLevel.HIGH
        }

        val approvedReports = reports.count {
            it.status == ReportStatus.APPROVED
        }

        val resolvedPercentage =
            if (totalReports > 0)
                approvedReports * 100.0 / totalReports
            else
                0.0

        return StatisticsResponse(
            totalReports = totalReports,
            reportsThisWeek = reportsThisWeek,
            reportsThisMonth = reportsThisMonth,
            mostReportedCategory = mostReportedCategory,
            averageVotesPerReport = averageVotesPerReport,
            topPriorityReports = topPriorityReports,
            resolvedPercentage = resolvedPercentage
        )
    }
}