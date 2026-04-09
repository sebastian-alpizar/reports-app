package com.example.mobile.domain.validation

import javax.inject.Inject

class PasswordValidator @Inject constructor() {

    fun validate(password: String): String? {

        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }

        return null
    }
}