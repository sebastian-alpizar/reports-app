package org.example.reports.infrastructure.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    private val SECRET = "my-secret-key-my-secret-key-my-secret-key" // mínimo 32 chars
    private val key = Keys.hmacShaKeyFor(SECRET.toByteArray())

    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key)
            .compact()
    }

    fun extractEmail(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}