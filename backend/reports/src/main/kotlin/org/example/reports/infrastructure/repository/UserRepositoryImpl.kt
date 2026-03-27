package org.example.reports.infrastructure.repository

import org.example.reports.domain.model.User
import org.example.reports.domain.repository.UserRepository
import org.example.reports.infrastructure.mapper.UserMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository,
    private val mapper: UserMapper
) : UserRepository {

    override fun findById(id: Long): User? {
        return jpaRepository.findById(id)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }

    override fun save(user: User): User {
        val entity = mapper.toEntity(user)
        val saved = jpaRepository.save(entity)
        return mapper.toDomain(saved)
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaRepository.existsByEmail(email)
    }

    override fun existsByNationalId(id: String): Boolean {
        return jpaRepository.existsByNationalId(id)
    }
}