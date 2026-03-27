package org.example.reports.presentation.dto

data class CreateUserRequest(
    val name: String,
    val email: String,
    val nationalId: String,
    val password: String
)