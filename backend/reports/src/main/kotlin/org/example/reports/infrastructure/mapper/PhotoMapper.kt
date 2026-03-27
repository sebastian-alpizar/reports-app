package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.Photo
import org.example.reports.infrastructure.entity.PhotoEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", uses = [ReportMapper::class])
interface PhotoMapper {

    fun toDomain(entity: PhotoEntity): Photo

    fun toEntity(domain: Photo): PhotoEntity
}