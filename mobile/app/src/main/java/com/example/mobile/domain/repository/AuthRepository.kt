package com.example.mobile.domain.repository

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<String>

    suspend fun register(
        name: String,
        email: String,
        nationalId: String,
        password: String
    ): Result<String>
}