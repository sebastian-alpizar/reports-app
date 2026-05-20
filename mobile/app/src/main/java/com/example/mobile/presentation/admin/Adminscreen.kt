package com.example.mobile.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.presentation.components.AppBackground
import kotlinx.coroutines.launch

private val AccentPurple = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg = Color.White.copy(alpha = 0.52f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppBackground {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(290.dp),
                    drawerContainerColor = Color.Transparent,
                    drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.7f))
                    ) {
                        Spacer(
                            Modifier
                                .windowInsetsPadding(WindowInsets.statusBars)
                                .height(0.dp)
                        )

                        // ── HEADER ───────────────────────────────────
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AccentPurpleLight, AccentPurple)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "A",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Administrador",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    "admin@system.com",
                                    color = Color.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // ── ITEM: REPORTES ───────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    scope.launch { drawerState.close() }
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AccentPurpleLight, AccentPurple)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(
                                "Reportes",
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = Color.White
                        )

                        Spacer(Modifier.height(4.dp))

                        // ── ITEM: CERRAR SESIÓN ──────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 2.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(
                                "Cerrar sesión",
                                color = Color(0xFFB71C1C),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.fillMaxSize()) {

                    // ── TOP BAR ──────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardBg)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menú",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Panel de Administración",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                Text(
                                    text = "${uiState.reports.size} reportes",
                                    fontSize = 11.sp,
                                    color = Color.Black.copy(alpha = 0.45f)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AccentPurple.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { viewModel.loadAllReports() },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Actualizar",
                                        tint = AccentPurple,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                        }
                    }

                    when {
                        uiState.isLoading -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = AccentPurpleLight)
                            }
                        }

                        uiState.error != null -> {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(uiState.error ?: "", color = Color.Black)
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(uiState.reports) { report ->
                                    AdminReportCard(
                                        report = report,
                                        isLoading = uiState.updatingId == report.id,
                                        onStatusChange = { status ->
                                            viewModel.updateStatus(report.id, status)
                                        },
                                        onDelete = {
                                            // viewModel.deleteReport(report.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportCard(
    report: ReportResponse,
    isLoading: Boolean,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val statusColor = when (report.status?.lowercase()) {
        "pendiente"  -> Color(0xFFF59E0B)
        "en_proceso" -> Color(0xFF3B82F6)
        "resuelto"   -> Color(0xFF10B981)
        else         -> AccentPurple
    }

    val statusTextColor = when (report.status?.lowercase()) {
        "pendiente"  -> Color(0xFF92400E)
        "en_proceso" -> Color(0xFF1E3A5F)
        "resuelto"   -> Color(0xFF064E3B)
        else         -> Color(0xFF3C3489)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(16.dp)
    ) {
        Column {

            // ── ICON + DESCRIPTION ───────────────────────
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AccentPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = report.description,
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.72f),
                    lineHeight = 19.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(13.dp))

            HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))

            Spacer(Modifier.height(11.dp))

            // ── FOOTER: STATUS + DATE + DELETE ───────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Surface(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(50.dp),
                        color = statusColor.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = report.status ?: "Sin estado",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusTextColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = statusTextColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("pendiente", "en_proceso", "resuelto").forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = status.replaceFirstChar { it.uppercase() }
                                            .replace("_", " ")
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    onStatusChange(status)
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = report.reportDate,
                        fontSize = 11.sp,
                        color = Color.Black.copy(alpha = 0.42f)
                    )

                    Spacer(Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { showDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(
                    color = AccentPurpleLight,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Eliminar reporte",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E1B4B)
                )
            },
            text = {
                Text(
                    "¿Seguro que querés eliminar este reporte? Esta acción no se puede deshacer.",
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Eliminar", color = Color.White, fontSize = 13.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar", fontSize = 13.sp)
                }
            }
        )
    }
}