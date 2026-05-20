package com.example.mobile.domain.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val nationalId: String,
    val isAdmin: Boolean
)