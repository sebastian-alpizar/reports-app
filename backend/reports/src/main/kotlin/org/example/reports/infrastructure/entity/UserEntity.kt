package org.example.reports.infrastructure.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, unique = true, length = 150)
    val email: String,

    @Column(name = "national_id", nullable = false, unique = true, length = 50)
    val nationalId: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "is_admin")
    val isAdmin: Boolean = false
)