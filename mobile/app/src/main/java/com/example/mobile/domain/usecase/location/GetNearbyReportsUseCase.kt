package com.example.mobile.domain.usecase.location

import com.example.mobile.domain.model.Report
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class GetNearbyReportsUseCase  @Inject constructor(
    private val repository: ReportRepository
){
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 5.0
    ): Result<List<Report>> {
        return repository.getNearbyReports(latitude, longitude, radiusKm)
    }
}