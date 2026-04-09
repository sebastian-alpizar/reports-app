package com.example.mobile.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.8f)) },
        modifier = modifier,
        isError = isError,
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),

        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Icon(
                    if (passwordVisible) Icons.Filled.VisibilityOff
                    else Icons.Filled.Visibility,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },

        textStyle = LocalTextStyle.current.copy(color = Color.White),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedLabelColor = Color.White.copy(alpha = 0.9f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
            cursorColor = Color.White
        ),

        shape = RoundedCornerShape(16.dp)
    )
}