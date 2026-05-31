package org.example.reports.infrastructure.repository

import org.example.reports.domain.model.Vote
import org.example.reports.domain.repository.VoteRepository
import org.example.reports.infrastructure.mapper.VoteMapper
import org.springframework.stereotype.Repository

@Repository
class VoteRepositoryImpl(
    private val jpaRepository: VoteJpaRepository,
    private val mapper: VoteMapper
): VoteRepository {
    override fun save(vote: Vote): Vote {
        val entity = mapper.toEntity(vote)
        return mapper.toDomain(
            jpaRepository.save(entity)
        )
    }

    override fun existsByReportIdAndUserId(
        reportId: Long,
        userId: Long
    ): Boolean {
        return jpaRepository.existsByReport_IdAndUser_Id(
            reportId,
            userId
        )
    }

    override fun countByReportId(
        reportId: Long
    ): Int {
        return jpaRepository.countByReport_Id(
            reportId
        )
    }

    override fun deleteByReportId(reportId: Long) {
        jpaRepository.deleteByReport_Id(reportId)
    }
}