package org.example.reports.presentation.controller

import org.example.reports.application.usecase.photos.*
import org.example.reports.presentation.dto.CreatePhotoRequest
import org.example.reports.presentation.dto.PhotoResponse
import org.example.reports.presentation.mapper.PhotoDtoMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/photos")
class PhotoController(
    private val addPhotoUseCase: AddPhotoToReportUseCase,
    private val getPhotosByReportUseCase: GetPhotosByReportUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val mapper: PhotoDtoMapper
) {

    @PostMapping
    fun addPhoto(@RequestBody request: CreatePhotoRequest): PhotoResponse {
        return mapper.toResponse(addPhotoUseCase.execute(request))
    }

    @GetMapping("/report/{reportId}")
    fun getByReport(@PathVariable reportId: Long): List<PhotoResponse> {
        return getPhotosByReportUseCase.execute(reportId)
            .map { mapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        deletePhotoUseCase.execute(id)
    }
}