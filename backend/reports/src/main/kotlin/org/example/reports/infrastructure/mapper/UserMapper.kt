package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.User
import org.example.reports.infrastructure.entity.UserEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {

    fun toDomain(entity: UserEntity): User

    fun toEntity(domain: User): UserEntity
}