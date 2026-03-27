package org.example.reports.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nationalId: String,
    val password: String,
    val isAdmin: Boolean
)