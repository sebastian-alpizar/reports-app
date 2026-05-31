package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.VoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.transaction.annotation.Transactional

interface VoteJpaRepository  : JpaRepository<VoteEntity, Long> {
    fun existsByReport_IdAndUser_Id(
        reportId: Long,
        userId: Long
    ): Boolean

    fun countByReport_Id(
        reportId: Long
    ): Int

    @Modifying
    @Transactional
    fun deleteByReport_Id(reportId: Long)
}