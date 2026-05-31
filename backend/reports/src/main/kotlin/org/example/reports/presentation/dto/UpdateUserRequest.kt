package org.example.reports.presentation.dto

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val nationalId: String? = null,
    val isAdmin: Boolean? = null
)