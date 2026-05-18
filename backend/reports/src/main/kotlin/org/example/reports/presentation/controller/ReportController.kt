package org.example.reports.presentation.controller

import org.example.reports.application.usecase.reports.CreateReportUseCase
import org.example.reports.application.usecase.reports.DeleteReportUseCase
import org.example.reports.application.usecase.reports.GetReportsUseCase
import org.example.reports.presentation.dto.ApiResponse
import org.example.reports.presentation.dto.CreateReportRequest
import org.example.reports.presentation.dto.ReportResponse
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
    private val mapper: ReportDtoMapper,
) {
    @PostMapping(consumes = ["multipart/form-data"])
    fun createReportWithPhoto(
        @RequestPart("report") reportData: CreateReportRequest,  // Los datos del reporte como JSON
        @RequestPart("photo") photo: MultipartFile // El archivo de la foto
    ): ResponseEntity<ReportResponse> {
        return try {
            val savedReport = createReportUseCase.execute(reportData, photo)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(savedReport))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

//    ): ResponseEntity<ApiResponse<Unit>> {
//        return try {
//            val request = CreateReportRequest(
//                description = reportData.description,
//                latitude = reportData.latitude,
//                longitude = reportData.longitude,
//                approximateLocation = reportData.approximateLocation,
//                category = reportData.category,
//            )
//            createReportUseCase.execute(request, photo)
//            ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse(message = "Reporte creado exitosamente"))
//        } catch (e: Exception) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(ApiResponse(message = "Error al crear el reporte: ${e.message}"))
//        }
//    }

    /**
     * GET /api/reports/nearby?lat=9.93&lng=-84.08&radiusKm=5
     * Devuelve los reportes dentro del radio indicado alrededor de la coordenada.
     */
    @GetMapping("/nearby")
    fun getNearby(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(defaultValue = "5.0") radiusKm: Double
    ): List<ReportResponse> {
        return reportQueryService.getNearbyReports(lat, lng, radiusKm)
            .map { mapper.toResponse(it) }
    }

    /**
     * GET /api/reports/my
     * Reportes del usuario autenticado (ya existía como /user/{userId},
     * pero este endpoint no requiere pasar el ID en la URL).
     */
    @GetMapping("/my")
    fun getMyReports(): List<ReportResponse> {
        // Reutiliza getAll por ahora; si quieres filtrar por usuario autenticado
        return reportQueryService.getAllReports()
            .map { mapper.toResponse(it) }
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
}