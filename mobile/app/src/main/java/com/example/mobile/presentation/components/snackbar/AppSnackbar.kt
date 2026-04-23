package com.example.mobile.presentation.components.snackbar

import androidx.compose.runtime.Composable

@Composable
fun AppSnackbar(
    message: String,
    isError: Boolean = false,
    visible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 3000L
) {
    AnimatedTopSnackbar(
        message = message,
        isError = isError,
        visible = visible,
        onDismiss = onDismiss,
        duration = duration
    )
}