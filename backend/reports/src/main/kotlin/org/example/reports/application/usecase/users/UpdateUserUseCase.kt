package org.example.reports.application.usecase.users

import org.example.reports.domain.model.User
import org.example.reports.domain.repository.UserRepository
import org.example.reports.presentation.dto.UpdateUserRequest
import org.springframework.stereotype.Service

@Service
class UpdateUserUseCase(
    private val userRepository: UserRepository
) {

    fun execute(id: Long, request: UpdateUserRequest): User {
        val existing = userRepository.findById(id)
            ?: throw RuntimeException("Usuario no encontrado")

        if (request.email != null && request.email != existing.email) {
            if (userRepository.existsByEmailAndIdNot(request.email, id))
                throw IllegalArgumentException("El correo ya está en uso")
        }

        if (request.nationalId != null && request.nationalId != existing.nationalId) {
            if (userRepository.existsByNationalIdAndIdNot(request.nationalId, id))
                throw IllegalArgumentException("La cédula ya está en uso")
        }

        val updated = existing.copy(
            name = request.name ?: existing.name,
            email = request.email ?: existing.email,
            nationalId = request.nationalId ?: existing.nationalId,
            isAdmin = request.isAdmin ?: existing.isAdmin
        )

        return userRepository.save(updated)
    }
}