package org.example.reports.infrastructure.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "photos")
data class PhotoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val url: String,

    @Column(name = "upload_date")
    val uploadDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "ai_validated")
    val aiValidated: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "report_id")
    val report: ReportEntity
)