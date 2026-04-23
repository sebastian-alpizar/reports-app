package com.example.mobile.presentation.components.snackbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SnackbarState {
    var isVisible by mutableStateOf(false)
        private set
    var message by mutableStateOf("")
        private set
    var isError by mutableStateOf(false)
        private set

    fun show(message: String, isError: Boolean = false) {
        this.message = message
        this.isError = isError
        isVisible = true
    }

    suspend fun dismiss() {
        isVisible = false
    }
}