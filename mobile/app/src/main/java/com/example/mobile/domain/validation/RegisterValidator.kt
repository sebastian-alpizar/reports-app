package com.example.mobile.domain.validation

import javax.inject.Inject

class RegisterValidator @Inject constructor(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator
) {
    fun validate(
        name: String,
        email: String,
        nationalId: String,
        password: String
    ): String? {

        if (name.isBlank()) return "El nombre es obligatorio"
        if (nationalId.isBlank()) return "La cédula es obligatoria"

        emailValidator.validate(email)?.let { return it }
        passwordValidator.validate(password)?.let { return it }

        return null
    }
}