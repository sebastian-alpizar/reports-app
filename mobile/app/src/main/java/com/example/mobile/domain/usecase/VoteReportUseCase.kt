package com.example.mobile.domain.usecase

import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class VoteReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(reportId: Long): Result<String> {
        return repository.voteReport(reportId)
    }
}