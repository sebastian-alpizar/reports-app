package org.example.reports.infrastructure.security

import org.example.reports.application.security.AuthenticatedUserProvider
import org.example.reports.domain.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SpringSecurityUserProvider(
    private val userRepository: UserRepository
) : AuthenticatedUserProvider {

    override fun getCurrentUserEmail(): String {
        return SecurityContextHolder.getContext().authentication?.name
            ?: throw RuntimeException("Usuario no autenticado")
    }

    fun getCurrentUserId(): Long {
        val email = getCurrentUserEmail()

        return userRepository.findByEmail(email)?.id
            ?: throw RuntimeException("Usuario no encontrado")
    }
}