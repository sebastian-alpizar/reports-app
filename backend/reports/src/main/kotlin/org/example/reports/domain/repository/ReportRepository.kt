package org.example.reports.domain.repository

import org.example.reports.domain.model.Report
import org.example.reports.domain.model.ReportStatus

interface ReportRepository {

    fun findById(id: Long): Report?

    fun findAll(): List<Report>

    fun findByUserId(userId: Long): List<Report>

    fun save(report: Report): Report

    fun deleteById(id: Long)
    fun findNearby(lat: Double, lng: Double, radiusKm: Double): List<Report>

    fun updateStatus(
        reportId: Long,
        status: ReportStatus
    )
}