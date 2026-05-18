package org.example.reports.application.usecase.auth

import org.example.reports.domain.repository.UserRepository
import org.example.reports.infrastructure.config.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun execute(email: String, password: String): String {

        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Usuario no encontrado")

        if (!passwordEncoder.matches(password, user.password)) {
            throw RuntimeException("Credenciales inválidas")
        }

        return jwtService.generateToken(user.email)
    }
}