package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.PriorityLevel
import org.example.reports.domain.model.Report
import org.example.reports.domain.model.ReportStatus
import org.example.reports.domain.repository.PhotoRepository
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.domain.repository.UserRepository
import org.example.reports.domain.repository.VoteRepository
import org.example.reports.infrastructure.security.SpringSecurityUserProvider
import org.springframework.stereotype.Service

@Service
class GetReportsUseCase(
    private val reportRepository: ReportRepository,
    private val photoRepository: PhotoRepository,
    private val voteRepository: VoteRepository,
    private val authProvider: SpringSecurityUserProvider,
) {
    fun getAllReports(): List<ReportWithMetadata> {
        val currentUserId = authProvider.getCurrentUserId()

        return reportRepository.findAll()
            .map { it.toMetadata(currentUserId) }
            .sortedWith(
                compareBy<ReportWithMetadata> {
                    when (it.report.status) {
                        ReportStatus.PENDING -> 0
                        ReportStatus.APPROVED -> 1
                        ReportStatus.REJECTED -> 2
                    }
                }.thenByDescending {
                    PriorityCalculator.calculate(
                        it.report,
                        it.affectedUsers
                    )
                }
            )
    }

    fun getReportById(id: Long): ReportWithMetadata {
        val currentUserId = authProvider.getCurrentUserId()

        return reportRepository.findById(id)
            ?.toMetadata(currentUserId)
            ?: throw RuntimeException("Reporte no encontrado")
    }

    fun getReportsByUser(userId: Long): List<ReportWithMetadata> {
        val currentUserId = authProvider.getCurrentUserId()
        return reportRepository.findByUserId(userId)
            .map { it.toMetadata(currentUserId) }
    }

    fun getNearbyReports(lat: Double, lng: Double, radiusKm: Double = 5.0): List<ReportWithMetadata> {
        val currentUserId = authProvider.getCurrentUserId()
        return reportRepository.findNearby(
            lat,
            lng,
            radiusKm
        ).map { it.withPhoto().toMetadata(currentUserId) }
    }

    private fun Report.withPhoto(): Report {
        val photo = photoRepository.findByReportId(this.id).firstOrNull()
        return this.copy(photoUrl = photo?.url)
    }

    data class ReportWithMetadata(
        val report: Report,
        val affectedUsers: Int,
        val priorityLevel: PriorityLevel,
        val userHasVoted: Boolean
    )

    private fun Report.toMetadata(userId: Long): ReportWithMetadata {

        val affectedUsers =
            voteRepository.countByReportId(id)

        val priorityLevel =
            PriorityCalculator.getLabel(
                this,
                affectedUsers
            )

        val userHasVoted =
            voteRepository.existsByReportIdAndUserId(
                id,
                userId
            )

        return ReportWithMetadata(
            report = this,
            affectedUsers = affectedUsers,
            priorityLevel = priorityLevel,
            userHasVoted = userHasVoted
        )
    }
}