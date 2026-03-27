package org.example.reports.infrastructure.mapper

import org.example.reports.domain.model.Report
import org.example.reports.infrastructure.entity.ReportEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", uses = [UserMapper::class])
interface ReportMapper {

    fun toDomain(entity: ReportEntity): Report

    fun toEntity(domain: Report): ReportEntity
}