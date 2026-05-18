package org.example.reports.infrastructure.repository

import org.example.reports.domain.model.Report
import org.example.reports.domain.repository.ReportRepository
import org.example.reports.infrastructure.mapper.ReportMapper
import org.springframework.stereotype.Repository

@Repository
class ReportRepositoryImpl(
    private val jpaRepository: ReportJpaRepository,
    private val mapper: ReportMapper
) : ReportRepository {

    override fun findById(id: Long): Report? {
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findAll(): List<Report> {
        return jpaRepository.findAll()
            .map { mapper.toDomain(it) }
    }

    override fun findByUserId(userId: Long): List<Report> {
        return jpaRepository.findByUserId(userId)
            .map { mapper.toDomain(it) }
    }

    override fun save(report: Report): Report {
        val entity = mapper.toEntity(report)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }
}