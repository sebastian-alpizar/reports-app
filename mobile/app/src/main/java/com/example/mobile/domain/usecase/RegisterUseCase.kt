package com.example.mobile.domain.usecase

import com.example.mobile.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(
        name: String,
        email: String,
        nationalId: String,
        password: String
    ) = repository.register(name, email, nationalId, password)

}