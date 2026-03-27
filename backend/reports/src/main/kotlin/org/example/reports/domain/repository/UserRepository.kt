package org.example.reports.domain.repository

import org.example.reports.domain.model.User

interface UserRepository {

    fun findById(id: Long): User?

    fun findByEmail(email: String): User?

    fun save(user: User): User

    fun existsByEmail(email: String): Boolean

    fun existsByNationalId(id: String): Boolean
}