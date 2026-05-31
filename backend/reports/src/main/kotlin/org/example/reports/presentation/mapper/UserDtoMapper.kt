package org.example.reports.presentation.mapper

import org.example.reports.domain.model.User
import org.example.reports.presentation.dto.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserDtoMapper {

    @Mapping(source = "admin", target = "isAdmin")
    fun toResponse(user: User): UserResponse
}