package com.example.mobile.domain.usecase

import com.example.mobile.domain.model.Report
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class GetReportsByUserUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(userId: Long): List<Report> {
        return reportRepository.getReportsByUser(userId).getOrThrow()
    }
}