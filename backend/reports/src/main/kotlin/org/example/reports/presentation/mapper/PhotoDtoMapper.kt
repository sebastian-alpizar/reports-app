package org.example.reports.presentation.mapper

import org.example.reports.domain.model.Photo
import org.example.reports.presentation.dto.PhotoResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PhotoDtoMapper {

    @Mapping(source = "report.id", target = "reportId")
    fun toResponse(photo: Photo): PhotoResponse
}