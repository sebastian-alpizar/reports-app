package org.example.reports.application.usecase.reports

import org.example.reports.domain.model.Vote
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.domain.repository.UserRepository
import org.example.reports.domain.repository.VoteRepository
import org.example.reports.infrastructure.security.SpringSecurityUserProvider
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class VoteUseCase (
    private val authProvider: SpringSecurityUserProvider,
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val voteRepository: VoteRepository
) {
    fun execute (id: Long) {
        val email = authProvider.getCurrentUserEmail()

        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Usuario no encontrado")

        val report = reportRepository.findById(id)
            ?: throw RuntimeException("Reporte no encontrado")

        if (
            voteRepository.existsByReportIdAndUserId(
                report.id,
                user.id
            )
        ) {
            throw RuntimeException(
                "Ya votaste este reporte"
            )
        }

        val vote = Vote(
            id = 0,
            report = report,
            user = user,
            voteDate = LocalDateTime.now()
        )

        voteRepository.save(vote)
    }

}