package org.example.reports.domain.repository

import org.example.reports.domain.model.Report

interface ReportRepository {
    fun findById(id: Long): Report?
    fun findAll(): List<Report>
    fun findByUserId(userId: Long): List<Report>
    fun findNearby(lat: Double, lng: Double, radiusKm: Double): List<Report>
    fun save(report: Report): Report
    fun deleteById(id: Long)
}