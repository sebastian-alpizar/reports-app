package org.example.reports.presentation.controller

import org.example.reports.application.usecase.reports.CreateReportUseCase
import org.example.reports.application.usecase.reports.DeleteReportUseCase
import org.example.reports.application.usecase.reports.GetReportsUseCase
import org.example.reports.application.usecase.reports.UpdateReportStatusUseCase
import org.example.reports.application.usecase.reports.UpdateReportUseCase
import org.example.reports.presentation.dto.ApiResponse
import org.example.reports.presentation.dto.CreateReportRequest
import org.example.reports.presentation.dto.ReportResponse
import org.example.reports.presentation.dto.UpdateReportRequest
import org.example.reports.presentation.dto.UpdateReportStatusRequest
import org.example.reports.presentation.mapper.ReportDtoMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/reports")
class ReportController(
    private val createReportUseCase: CreateReportUseCase,
    private val reportQueryService: GetReportsUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase,
    private val updateReportUseCase: UpdateReportUseCase,
    private val mapper: ReportDtoMapper,
) {
    @PostMapping(consumes = ["multipart/form-data"])
    fun createReportWithPhoto(
        @RequestPart("report") reportData: CreateReportRequest,  // Los datos del reporte como JSON
        @RequestPart("photo") photo: MultipartFile               // El archivo de la foto
    ): ResponseEntity<ApiResponse<Unit>> {
        return try {
            val request = CreateReportRequest(
                description = reportData.description,
                latitude = reportData.latitude,
                longitude = reportData.longitude,
                approximateLocation = reportData.approximateLocation,
                category = reportData.category,
            )
            createReportUseCase.execute(request, photo)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse(message = "Reporte creado exitosamente"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse(message = "Error al crear el reporte: ${e.message}"))
        }
    }

    @GetMapping
    fun getAll(): List<ReportResponse> {
        return reportQueryService.getAllReports()
            .map { mapper.toResponse(it) }
    }

    @GetMapping("/nearby")
    fun getNearby(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "5.0") radiusKm: Double
    ): List<ReportResponse> {
        return reportQueryService.getNearbyReports(lat, lng, radiusKm)
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

    @PutMapping("/{id}", consumes = ["multipart/form-data"])
    fun updateReport(
        @PathVariable id: Long,
        @RequestPart("report") reportData: UpdateReportRequest,
        @RequestPart(value = "photo", required = false) photo: MultipartFile?
    ): ResponseEntity<ApiResponse<ReportResponse>> {
        return try {
            val updated = updateReportUseCase.execute(id, reportData, photo)
            ResponseEntity.ok(
                ApiResponse(message = "Reporte actualizado exitosamente", data = mapper.toResponse(updated))
            )
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse(message = e.message ?: "Sin permiso"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse(message = "Error: ${e.message}"))
        }
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        deleteReportUseCase.execute(id)
        return ResponseEntity.ok(
            ApiResponse(
                message = "Reporte eliminado exitosamente"
            )
        )
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateReportStatusRequest
    ): ResponseEntity<ApiResponse<Unit>> {

        updateReportStatusUseCase.execute(
            reportId = id,
            status = request.status
        )

        return ResponseEntity.ok(
            ApiResponse(
                message = "Estado actualizado exitosamente"
            )
        )
    }
}