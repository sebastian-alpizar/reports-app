package org.example.reports.presentation.mapper

import org.example.reports.domain.model.Report
import org.example.reports.presentation.dto.ReportResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface ReportDtoMapper {

    @Mapping(source = "user.id", target = "userId")
    fun toResponse(report: Report): ReportResponse
}