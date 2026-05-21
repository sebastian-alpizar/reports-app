package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.User
import org.example.reports.infrastructure.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            nationalId = entity.nationalId,
            password = entity.password,
            isAdmin = entity.isAdmin  // ← mapeo explícito
        )
    }

    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            nationalId = domain.nationalId,
            password = domain.password,
            isAdmin = domain.isAdmin  // ← mapeo explícito
        )
    }
}