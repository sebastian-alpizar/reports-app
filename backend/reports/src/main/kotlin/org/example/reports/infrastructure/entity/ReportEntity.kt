package org.example.reports.infrastructure.entity

import jakarta.persistence.*
import org.example.reports.domain.model.ReportStatus
import java.time.LocalDateTime

@Entity
@Table(name = "reports")
data class ReportEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val description: String,

    val latitude: Double?,

    val longitude: Double?,

    @Column(name = "approximate_location")
    val approximateLocation: String?,

    @Column(name = "report_date")
    val reportDate: LocalDateTime = LocalDateTime.now(),

    val category: String?,

    @Enumerated(EnumType.STRING)
    val status: ReportStatus,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity
)