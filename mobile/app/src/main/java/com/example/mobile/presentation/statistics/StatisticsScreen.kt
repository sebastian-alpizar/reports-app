package com.example.mobile.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.components.AppBackground

private val CardBg = Color.White.copy(alpha = 0.5f)

// Colores más suaves para diferentes tipos de estadísticas
private val PrimaryColor = Color(0xFF818CF8)      // Indigo más suave
private val SuccessColor = Color(0xFF6EE7B7)     // Verde más suave
private val WarningColor = Color(0xFFFCD34D)     // Amarillo más suave
private val DangerColor = Color(0xFFFCA5A5)      // Rojo más suave
private val InfoColor = Color(0xFF7DD3FC)        // Azul más suave
private val PurpleColor = Color(0xFFB172F3)      // Púrpura más suave
private val AccentPurple = Color(0xFF7C3AED)

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state = viewModel.uiState

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            StatisticsTopBar(navController = navController)

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                }

                state.statistics != null -> {
                    val stats = state.statistics

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 32.dp
                        )
                    ) {
                        // Primera fila: KPIs principales
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiCard(
                                    title = "Total Reportes",
                                    value = stats.totalReports.toString(),
                                    icon = Icons.Default.BarChart,
                                    color = PrimaryColor,
                                    modifier = Modifier.weight(1f)
                                )

                                KpiCard(
                                    title = "Esta Semana",
                                    value = stats.reportsThisWeek.toString(),
                                    icon = Icons.Default.CalendarToday,
                                    color = InfoColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Segunda fila: KPIs secundarios
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                KpiCard(
                                    title = "Este Mes",
                                    value = stats.reportsThisMonth.toString(),
                                    icon = Icons.Default.DateRange,
                                    color = SuccessColor,
                                    modifier = Modifier.weight(1f)
                                )

                                KpiCard(
                                    title = "Alta Prioridad",
                                    value = stats.topPriorityReports.toString(),
                                    icon = Icons.Default.Warning,
                                    color = DangerColor,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Categoría más reportada
                        item {
                            HighlightCard(
                                title = "Categoría Más Reportada",
                                value = stats.mostReportedCategory ?: "Sin datos",
                                icon = Icons.Default.Category,
                                color = PurpleColor
                            )
                        }

                        // Promedio de Votos por Reporte (simplificado)
                        item {
                            StatisticCard(
                                title = "Promedio de Votos por Reporte",
                                value = String.format("%.1f", stats.averageVotesPerReport)
                            )
                        }

                        // Porcentaje resuelto
                        item {
                            CircularProgressCard(
                                title = "Tasa de Resolución",
                                percentage = stats.resolvedPercentage,
                                icon = Icons.Default.CheckCircle,
                                color = SuccessColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticsTopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            IconButton(onClick = { navController.popBackStack() }) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            AccentPurple.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {

                Text(
                    text = "Estadísticas",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.2.sp
                )

                Text(
                    text = "Resumen general de reportes",
                    color = Color.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }

            Column {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun HighlightCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = value,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
private fun CircularProgressCard(
    title: String,
    percentage: Double,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "${percentage.toInt()}% completado",
                        fontSize = 12.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }

            // Gráfico circular
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = (percentage / 100f).toFloat(),
                    modifier = Modifier.fillMaxSize(),
                    color = color,
                    strokeWidth = 6.dp,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Text(
                    text = "${percentage.toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

