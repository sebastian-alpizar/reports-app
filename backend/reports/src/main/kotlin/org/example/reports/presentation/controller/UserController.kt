package org.example.reports.presentation.controller

import org.example.reports.application.usecase.auth.RegisterUseCase
import org.example.reports.application.usecase.users.GetUsersUseCase
import org.example.reports.application.usecase.users.UpdateUserUseCase
import org.example.reports.presentation.dto.ApiResponse
import org.example.reports.presentation.dto.CreateUserRequest
import org.example.reports.presentation.dto.UpdateUserRequest
import org.example.reports.presentation.dto.UserResponse
import org.example.reports.presentation.mapper.UserDtoMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: RegisterUseCase,
    private val userQueryService: GetUsersUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val mapper: UserDtoMapper
) {

    @PostMapping
    fun createUser(
        @RequestBody request: CreateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {

        return try {
            val saved = createUserUseCase.execute(request)
            println(saved)
            ResponseEntity.status(HttpStatus.CREATED)
                .body(
                    ApiResponse(
                        message = "Usuario creado exitosamente",
                        data = mapper.toResponse(saved)
                    )
                )
        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    ApiResponse(
                        message = "Error al crear el usuario: ${e.message}"
                    )
                )
        }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        return mapper.toResponse(userQueryService.getUserById(id))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val updated = updateUserUseCase.execute(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    message = "Usuario actualizado exitosamente",
                    data = mapper.toResponse(updated)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse(message = e.message ?: "Datos inválidos"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse(message = "Error al actualizar: ${e.message}"))
        }
    }
}