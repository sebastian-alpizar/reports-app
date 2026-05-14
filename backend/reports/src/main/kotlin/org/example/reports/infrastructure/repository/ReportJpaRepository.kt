package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReportJpaRepository : JpaRepository<ReportEntity, Long> {
    fun findByUserId(userId: Long): List<ReportEntity>

    /**
     * Reportes dentro de un radio usando la fórmula de Haversine.
     * No requiere columnas ni tablas adicionales; usa latitude y longitude existentes.
     */
    @Query(
        value = """
            SELECT * FROM reports
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
    fun findNearby(
        lat: Double,
        lng: Double,
        radiusKm: Double
    ): List<ReportEntity>
}