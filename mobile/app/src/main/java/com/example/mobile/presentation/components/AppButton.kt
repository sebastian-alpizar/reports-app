package com.example.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(
    text: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val buttonEnabled = enabled && !isLoading
    val backgroundBrush =
        if (buttonEnabled) {
            Brush.horizontalGradient(
                listOf(
                    Color(0xFF7C3AED),
                    Color(0xFFA78BFA)
                )
            )
        } else {
            Brush.horizontalGradient(
                listOf(
                    Color.LightGray,
                    Color.Gray
                )
            )
        }

    Button(
        onClick = onClick,
        enabled = buttonEnabled,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundBrush),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                color =
                    if (buttonEnabled) Color.White
                    else Color.DarkGray
            )
        }
    }
}