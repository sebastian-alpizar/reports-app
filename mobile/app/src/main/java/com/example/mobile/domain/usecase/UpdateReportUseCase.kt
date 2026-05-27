package com.example.mobile.domain.usecase

import android.content.Context
import com.example.mobile.domain.repository.ReportRepository
import javax.inject.Inject

class UpdateReportUseCase  @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(
        context: Context,
        reportId: String,
        description: String,
        imageUri: String?
    ): Result<Unit> {
        return repository.updateReport(context, reportId, description, imageUri)
    }
}