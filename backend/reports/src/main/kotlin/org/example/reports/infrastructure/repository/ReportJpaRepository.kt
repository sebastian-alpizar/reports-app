package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReportJpaRepository : JpaRepository<ReportEntity, Long> {
    fun findByUserId(userId: Long): List<ReportEntity>

    @Query(
        value = """
        SELECT id, description, latitude, longitude, approximate_location, 
               report_date, category, status, user_id, severity, 
               affected_users, priority_level
        FROM reports
        WHERE (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(latitude))
                * cos(radians(longitude) - radians(:lng))
                + sin(radians(:lat)) * sin(radians(latitude))
            )
        ) <= :radiusKm
        ORDER BY report_date DESC
    """,
        nativeQuery = true
    )
    fun findNearby(lat: Double, lng: Double, radiusKm: Double): List<ReportEntity>
}