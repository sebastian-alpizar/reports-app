package org.example.reports.application.usecase.reports

import org.example.reports.application.usecase.users.GetUsersUseCase
import org.example.reports.domain.model.Report
import org.example.reports.domain.model.ReportStatus
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.presentation.dto.CreateReportRequest
import org.springframework.stereotype.Service
import org.example.reports.infrastructure.security.SpringSecurityUserProvider
import java.time.LocalDateTime

@Service
class CreateReportUseCase(
    private val reportRepository: ReportRepository,
    private val authProvider: SpringSecurityUserProvider,
    private val userQueryService: GetUsersUseCase
) {

    fun execute(request: CreateReportRequest): Report {
        val email = authProvider.getCurrentUserEmail()
        val user = userQueryService.getUserByEmail(email)

        val report = Report(
            id = 0,
            description = request.description,
            latitude = request.latitude,
            longitude = request.longitude,
            approximateLocation = request.approximateLocation,
            reportDate = LocalDateTime.now(),
            category = request.category,
            status = ReportStatus.PENDING,
            user = user
        )

        return reportRepository.save(report)
    }
}