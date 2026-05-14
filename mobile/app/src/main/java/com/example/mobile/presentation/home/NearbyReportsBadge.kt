package com.example.mobile.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Badge flotante en la esquina inferior izquierda que muestra
 * el ícono de advertencia con la cantidad de reportes cercanos.
 * Al hacer clic, notifica al padre para mostrar el listado.
 */
@Composable
fun NearbyReportsBadge(
    reportCount: Int,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (reportCount > 0) 1f else 0.85f,
        animationSpec = tween(300),
        label = "badge_scale"
    )

    // Color del badge según urgencia
    val badgeColor = when {
        reportCount == 0 -> Color(0xFF6B7280).copy(alpha = 0.85f)   // gris sin reportes
        reportCount < 3  -> Color(0xFFF59E0B).copy(alpha = 0.90f)   // amarillo pocos
        else             -> Color(0xFFEF4444).copy(alpha = 0.90f)   // rojo muchos
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .scale(scale)
                .clip(RoundedCornerShape(16.dp))
                .background(badgeColor)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Reportes cercanos",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isLoading) "..." else "$reportCount cercanos",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}