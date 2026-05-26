package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.Report
import org.example.reports.infrastructure.entity.ReportEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValuePropertyMappingStrategy
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", uses = [UserMapper::class], unmappedSourcePolicy = ReportingPolicy.IGNORE)
interface ReportMapper {

    fun toDomain(entity: ReportEntity): Report

//    @Mapping(target = "photoUrl", ignore = true)
    fun toEntity(domain: Report): ReportEntity
}