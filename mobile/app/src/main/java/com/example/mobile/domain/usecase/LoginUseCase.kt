package com.example.mobile.domain.usecase

import com.example.mobile.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ) = repository.login(email, password)

}