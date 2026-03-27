package org.example.reports.infrastructure.security

import org.example.reports.domain.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {

        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")

        val authorities = if (user.isAdmin) {
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        } else {
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        }

        return User(
            user.email,
            user.password,
            authorities
        )
    }
}