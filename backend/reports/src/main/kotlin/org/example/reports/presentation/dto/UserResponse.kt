package org.example.reports.presentation.dto

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val isAdmin: Boolean
)