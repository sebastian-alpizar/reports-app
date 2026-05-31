package com.example.mobile.domain.usecase

import com.example.mobile.data.remote.dto.StatisticsResponse
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): Result<StatisticsResponse> {
        return reportRepository.getStatistics()
    }
}