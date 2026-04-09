package com.example.mobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFFCE93D8),
    tertiary = Color(0xFFA5D6A7),
    background = Color.Transparent,
    surface = Color.Transparent,
    surfaceContainer = Color.Transparent,
    surfaceContainerHigh = Color.Transparent,
    surfaceContainerLowest = Color.Transparent,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF7B1FA2),
    tertiary = Color(0xFF388E3C),
    background = Color.Transparent,
    surface = Color.Transparent,
    surfaceContainer = Color.Transparent,
    surfaceContainerHigh = Color.Transparent,
    surfaceContainerLowest = Color.Transparent,
)

@Composable
fun MobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Barra de estado translúcida
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as androidx.activity.ComponentActivity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}