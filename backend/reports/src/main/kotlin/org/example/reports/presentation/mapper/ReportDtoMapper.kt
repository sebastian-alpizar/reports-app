package org.example.reports.presentation.mapper

import org.example.reports.application.usecase.reports.GetReportsUseCase
import org.example.reports.application.usecase.reports.PriorityCalculator
import org.example.reports.domain.model.PriorityLevel
import org.example.reports.domain.model.Report
import org.example.reports.presentation.dto.ReportResponse
import org.springframework.stereotype.Component

@Component
class ReportDtoMapper {

    fun toResponse(metadata: GetReportsUseCase.ReportWithMetadata): ReportResponse {

        return ReportResponse(
            id = metadata.report.id,
            description = metadata.report.description,
            latitude = metadata.report.latitude,
            longitude = metadata.report.longitude,
            approximateLocation = metadata.report.approximateLocation,
            reportDate = metadata.report.reportDate,
            category = metadata.report.category,
            status = metadata.report.status.name,
            userName = metadata.report.user.name,
            userEmail = metadata.report.user.email,
            userId = metadata.report.user.id,
            photoUrl = metadata.report.photoUrl,

            severity = metadata.report.severity,
            affectedUsers = metadata.affectedUsers,
            priorityLevel = metadata.priorityLevel,
            userHasVoted = metadata.userHasVoted
        )
    }
}