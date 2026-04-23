package com.example.mobile.presentation.components.snackbar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AnimatedTopSnackbar(
    message: String,
    isError: Boolean = false,
    visible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 3000L
) {
    val slideInRight = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )

    val slideOutLeft = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(visible) {
        if (visible) {
            delay(duration)
            onDismiss()
        }
    }

    val fadeIn = fadeIn(animationSpec = tween(300))
    val fadeOut = fadeOut(animationSpec = tween(300))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn + slideInRight,
            exit = fadeOut + slideOutLeft
        ) {
            Box(
                modifier = Modifier
                    .offset(x = 55.dp)
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth(0.70f)
                    .background(
                        brush = if (isError) {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Red.copy(alpha = 0.85f),
                                    Color(0xFFFF6B6B).copy(alpha = 0.85f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF2E7D32).copy(alpha = 0.85f),
                                    Color(0xFF4CAF50).copy(alpha = 0.85f)
                                )
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}