package com.example.mobile.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppSnackbar(
    message: String,
    isError: Boolean = false
) {

    Snackbar(
        containerColor =
            if (isError) Color.Red
            else Color(0xFF2E7D32)
    ) {

        Text(
            text = message,
            color = Color.White
        )

    }

}