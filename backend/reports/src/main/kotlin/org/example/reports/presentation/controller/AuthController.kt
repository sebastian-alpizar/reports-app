package org.example.reports.presentation.controller

import org.example.reports.application.usecase.auth.LoginUseCase
import org.example.reports.presentation.dto.AuthResponse
import org.example.reports.presentation.dto.LoginRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val loginUseCase: LoginUseCase
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): AuthResponse {
        val token = loginUseCase.execute(request.email, request.password)
        return AuthResponse(token)
    }
}