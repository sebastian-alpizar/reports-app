package com.example.mobile.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label, color = Color.White.copy(alpha = 0.8f))
        },
        modifier = modifier,
        isError = isError,
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),

            focusedLabelColor = Color.White.copy(alpha = 0.9f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),

            cursorColor = Color.White,

            focusedContainerColor = Color.White.copy(alpha = 0.20f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.20f),
            disabledContainerColor = Color.White.copy(alpha = 0.20f),
            errorContainerColor = Color.White.copy(alpha = 0.20f)
        ),
        shape = RoundedCornerShape(16.dp)
    )
}