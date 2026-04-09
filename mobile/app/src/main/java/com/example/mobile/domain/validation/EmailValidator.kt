package com.example.mobile.domain.validation

import javax.inject.Inject
class EmailValidator @Inject constructor() {

    fun validate(email: String): String? {

        if (email.isBlank()) {
            return "El email es obligatorio"
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email inválido"
        }

        return null
    }
}