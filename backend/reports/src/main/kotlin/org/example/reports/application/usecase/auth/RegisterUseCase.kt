package org.example.reports.application.usecase.auth

import org.example.reports.domain.model.User
import org.example.reports.domain.repository.UserRepository
import org.example.reports.presentation.dto.CreateUserRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterUseCase(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun execute(request: CreateUserRequest): User {

        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("El email ya está en uso")
        }

        if (userRepository.existsByNationalId(request.nationalId)) {
            throw RuntimeException("La cédula ya está en uso")
        }

        val encryptedPassword = passwordEncoder.encode(request.password)

        val user = User(
            id = 0,
            name = request.name,
            email = request.email,
            nationalId = request.nationalId,
            password = request.password,
            isAdmin = false
        )

        val newUser = user.copy(password = encryptedPassword as String)

        return userRepository.save(newUser)
    }
}