package org.example.reports.presentation.mapper

import org.example.reports.domain.model.User
import org.example.reports.presentation.dto.UserResponse
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserDtoMapper {

    fun toResponse(user: User): UserResponse
}