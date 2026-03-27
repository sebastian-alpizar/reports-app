package org.example.reports.application.usecase.users

import org.example.reports.domain.model.User
import org.example.reports.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class GetUsersUseCase (
    private val userRepository: UserRepository,
) {
    fun getUserByIdU(id: Long): User {
        return userRepository.findById(id)
            ?: throw RuntimeException("Usuario no encontrado")
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw RuntimeException("Usuario no encontrado")
    }
}