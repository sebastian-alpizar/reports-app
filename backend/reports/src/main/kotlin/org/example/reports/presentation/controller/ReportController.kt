package org.example.reports.presentation.controller

import org.example.reports.application.usecase.reports.CreateReportUseCase
import org.example.reports.application.usecase.reports.DeleteReportUseCase
import org.example.reports.application.usecase.reports.GetReportsUseCase
import org.example.reports.presentation.dto.CreateReportRequest
import org.example.reports.presentation.dto.ReportResponse
import org.example.reports.presentation.mapper.ReportDtoMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val createReportUseCase: CreateReportUseCase,
    private val reportQueryService: GetReportsUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
    private val mapper: ReportDtoMapper,
) {

    @PostMapping
    fun createReport(@RequestBody request: CreateReportRequest): ReportResponse {
        return mapper.toResponse(createReportUseCase.execute(request))
    }

    @GetMapping
    fun getAll(): List<ReportResponse> {
        return reportQueryService.getAllReports()
            .map { mapper.toResponse(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ReportResponse {
        return mapper.toResponse(reportQueryService.getReportById(id))
    }

    @GetMapping("/user/{userId}")
    fun getByUser(@PathVariable userId: Long): List<ReportResponse> {
        return reportQueryService.getReportsByUser(userId)
            .map { mapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        deleteReportUseCase.execute(id)
    }
}