package org.example.reports.infrastructure.security

import org.example.reports.application.security.AuthenticatedUserProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SpringSecurityUserProvider : AuthenticatedUserProvider {

    override fun getCurrentUserEmail(): String {
        return SecurityContextHolder.getContext().authentication?.name
            ?: throw RuntimeException("Usuario no autenticado")
    }
}