package com.example.mobile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mobile.domain.model.Report
import kotlinx.coroutines.delay
private val AccentPurple = Color(0xFF7C3AED)

@Composable
fun ReportDetailCard(
    report: Report?,
    currentUserId: Long?,
    onDismiss: () -> Unit,
    onEditClicked: (Report) -> Unit,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(report) {
        if (report != null) {
            delay(8000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = report != null,
        enter   = slideInVertically { it },
        exit    = slideOutVertically { it },
        modifier = modifier
    ) {
        report ?: return@AnimatedVisibility

        val isMyReport = currentUserId != null && report.userId == currentUserId

        Surface(
            modifier        = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            shape           = RoundedCornerShape(20.dp),
            color           = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Header
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Warning,
                        contentDescription = null,
                        tint               = Color(0xFFF59E0B),
                        modifier           = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = report.category?.replaceFirstChar { it.uppercase() } ?: "Reporte",
                        color      = Color(0xFF1F1F1F),
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        modifier   = Modifier.weight(1f)
                    )
                    // Botón editar solo si es mi reporte
                    if (isMyReport) {
                        IconButton(
                            onClick = {
                                onDismiss()
                                onEditClicked(report)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.Edit,
                                contentDescription = "Editar reporte",
                                tint               = AccentPurple,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Imagen del reporte
                report.photoUrl?.let { url ->
                    AsyncImage(
                        model              = url,
                        contentDescription = "Foto del reporte",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Descripción
                Text(
                    text       = report.description,
                    color      = Color(0xFF1F1F1F).copy(alpha = 0.85f),
                    fontSize   = 14.sp,
                    lineHeight = 20.sp
                )

                // Ubicación aproximada
                report.approximateLocation?.let { loc ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint               = AccentPurple,
                            modifier           = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = loc, color = AccentPurple, fontSize = 12.sp)
                    }
                }

                // Estado
                report.status?.let { status ->
                    Spacer(modifier = Modifier.height(6.dp))
                    val statusColor = when (status.lowercase()) {
                        "activo", "pending"    -> Color(0xFFF59E0B)
                        "in_progress"          -> Color(0xFF3B82F6)
                        "resuelto", "resolved" -> Color(0xFF22C55E)
                        else                   -> Color.Gray
                    }
                    Text(
                        text       = "Estado: ${status.replaceFirstChar { it.uppercase() }}",
                        color      = statusColor,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                report.userName?.let { name ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text     = "Por: $name",
                        color    = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "%.5f, %.5f".format(
                        report.location.latitude,
                        report.location.longitude
                    ),
                    color    = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}