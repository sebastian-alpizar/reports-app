package com.example.mobile.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobile.domain.model.PriorityLevel
import com.example.mobile.domain.model.Report

private val AccentPurple = Color(0xFF7C3AED)
private val DangerRed    = Color(0xFFE53935)

@Composable
fun ReportDetailCard(
    isLoading: Boolean,
    report: Report?,
    currentUserId: Long?,
    onDismiss: () -> Unit,
    onEditClicked: (Report) -> Unit,
    onDeleteClicked: (Report) -> Unit,
    onVoteClicked: (Report) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog && report != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteClicked(report)
            },
            onDismiss = { showDeleteDialog = false }
        )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ==================== HEADER ====================
                HeaderSection(
                    category = report.category,
                    isMyReport = isMyReport,
                    onEdit = {
                        onDismiss()
                        onEditClicked(report)
                    },
                    onDelete = { showDeleteDialog = true }
                )

                // ==================== MÉTRICAS ====================
                MetricsSection(
                    priorityLevel = report.priorityLevel,
                    severity = report.severity,
                    affectedUsers = report.affectedUsers
                )

                // ==================== IMAGEN ====================
                report.photoUrl?.let { url ->
                    ReportImage(url = url)
                }

                // ==================== DESCRIPCIÓN ====================
                DescriptionSection(description = report.description)

                // ==================== UBICACIÓN ====================
                report.approximateLocation?.let { location ->
                    LocationSection(location = location)
                }

                // ==================== ESTADO ====================
                report.status?.let { status ->
                    StatusSection(status = status)
                }

                // ==================== BOTÓN DE VOTACIÓN ====================
                VoteButton(
                    hasVoted = report.userHasVoted,
                    onClick = { onVoteClicked(report) },
                    isLoading = isLoading
                )

                // ==================== METADATA ====================
                MetadataSection(
                    userName = report.userName,
                    latitude = report.location.latitude,
                    longitude = report.location.longitude
                )
            }
        }
    }
}

// ==================== COMPONENTES SECUNDARIOS ====================

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Eliminar reporte",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )
        },
        text = {
            Text(
                text = "¿Estás seguro que deseas eliminar este reporte? Esta acción no se puede deshacer.",
                color = Color(0xFF1F1F1F).copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = DangerRed, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = AccentPurple)
            }
        }
    )
}

@Composable
private fun HeaderSection(
    category: String?,
    isMyReport: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = category?.replaceFirstChar { it.uppercase() } ?: "Reporte",
            color = Color(0xFF1F1F1F),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        if (isMyReport) {
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar reporte",
                    tint = AccentPurple,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar reporte",
                    tint = DangerRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricsSection(
    priorityLevel: PriorityLevel,
    severity: Int,
    affectedUsers: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val priority = when (priorityLevel) {
            PriorityLevel.CRITICAL -> "Crítica"
            PriorityLevel.HIGH     -> "Alta"
            PriorityLevel.MEDIUM   -> "Media"
            else                   -> "Baja"
        }
        Text(
            text = "Prioridad: $priority",
            fontWeight = FontWeight.Bold,
            color = when (priorityLevel) {
                PriorityLevel.CRITICAL -> Color.Red
                PriorityLevel.HIGH     -> Color(0xFFFF6B00)
                PriorityLevel.MEDIUM   -> Color(0xFFF59E0B)
                else                   -> Color.Gray
            },
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Severidad: $severity/5",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$affectedUsers personas afectadas",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ReportImage(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Foto del reporte",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
private fun DescriptionSection(description: String) {
    Text(
        text = description,
        color = Color(0xFF1F1F1F).copy(alpha = 0.85f),
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
}

@Composable
private fun LocationSection(location: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = AccentPurple,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = location, color = AccentPurple, fontSize = 12.sp)
    }
}

@Composable
private fun StatusSection(status: String) {
    val (label, statusColor) = when (status.uppercase()) {
        "PENDING"  -> "Pendiente"  to Color(0xFFF59E0B)
        "REJECTED" -> "En proceso" to Color(0xFF3B82F6)
        "APPROVED" -> "Resuelto"   to Color(0xFF22C55E)
        else       -> status       to Color.Gray
    }

    Text(
        text       = "Estado: $label",
        color      = statusColor,
        fontSize   = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun VoteButton(hasVoted: Boolean, onClick: () -> Unit, isLoading: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AppButton(
            enabled = !hasVoted,
            text = if (hasVoted) "Ya votaste" else "Me afecta también",
            isLoading = isLoading,
            modifier = Modifier
                .defaultMinSize(minWidth = 200.dp, minHeight = 40.dp)
                .widthIn(max = 280.dp)
                .heightIn(max = 45.dp),
            onClick = onClick
        )
    }
}

@Composable
private fun MetadataSection(
    userName: String?,
    latitude: Double?,
    longitude: Double?
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        userName?.let {
            Text(
                text = "Por: $it",
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }

        Text(
            text = "%.5f, %.5f".format(latitude, longitude),
            color = Color.Gray.copy(alpha = 0.5f),
            fontSize = 10.sp
        )
    }
}