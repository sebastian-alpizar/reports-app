package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReportJpaRepository : JpaRepository<ReportEntity, Long> {

    fun findByUserId(userId: Long): List<ReportEntity>
}