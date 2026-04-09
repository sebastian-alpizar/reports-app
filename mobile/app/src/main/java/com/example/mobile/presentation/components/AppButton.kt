package com.example.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF7C3AED),
                        Color(0xFFA78BFA)
                    )
                )
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )
    ) {

        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, color = Color.White)
        }
    }
}