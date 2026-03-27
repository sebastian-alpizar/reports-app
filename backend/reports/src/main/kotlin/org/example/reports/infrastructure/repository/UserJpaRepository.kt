package org.example.reports.infrastructure.repository

import org.example.reports.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserJpaRepository : JpaRepository<UserEntity, Long> {

    fun findByEmail(email: String): Optional<UserEntity>

    fun existsByEmail(email: String): Boolean

    fun existsByNationalId(id: String): Boolean
}