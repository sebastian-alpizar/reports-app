package com.example.mobile.domain.usecase


import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class GetAllReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): List<ReportResponse> {
        return reportRepository.getAllReports().getOrThrow()
    }
}
