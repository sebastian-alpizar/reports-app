package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.PhotoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PhotoJpaRepository : JpaRepository<PhotoEntity, Long> {

    fun findByReportId(reportId: Long): List<PhotoEntity>
}