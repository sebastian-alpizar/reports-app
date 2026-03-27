package org.example.reports.presentation.controller

import org.example.reports.application.usecase.auth.RegisterUseCase
import org.example.reports.application.usecase.users.GetUsersUseCase
import org.example.reports.presentation.dto.CreateUserRequest
import org.example.reports.presentation.dto.UserResponse
import org.example.reports.presentation.mapper.UserDtoMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: RegisterUseCase,
    private val userQueryService: GetUsersUseCase,
    private val mapper: UserDtoMapper
) {

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest): UserResponse {
        val saved = createUserUseCase.execute(request)
        return mapper.toResponse(saved)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        return mapper.toResponse(userQueryService.getUserByIdU(id))
    }
}