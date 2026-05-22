package com.example.mobile.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object GlassModifiers {

    fun glassBackground(
        shape: Shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        tintColor: Color = Color.White,
        alpha: Float = 0.2f
    ): Modifier = Modifier
        .shadow(8.dp, shape, spotColor = Color.Black.copy(alpha = 0.1f))
        .background(
            Brush.linearGradient(
                colors = listOf(
                    tintColor.copy(alpha = alpha),
                    tintColor.copy(alpha = alpha * 0.7f)
                )
            ),
            shape = shape
        )
        .clip(shape)
        .alpha(0.95f)

    fun glassCard(
        shape: Shape = RoundedCornerShape(28.dp)
    ): Modifier =
        Modifier
            .clip(shape)
            .background(
                Color.White.copy(alpha = 0.7f),
                shape = shape
            )
}