package com.example.mobile.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.model.Report
import com.example.mobile.presentation.components.AppBackground
import com.example.mobile.presentation.utils.DateFormatter

// ── COLORES ───────────────────────────────────────────────────────────────────
private val AccentPurple      = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg            = Color.White.copy(alpha = 0.5f)

// ── ENUM DE ESTADOS (igual que AdminScreen) ───────────────────────────────────
private enum class ReportStatus(
    val apiValue : String,
    val label    : String,
    val dot      : Color,
    val bgColor  : Color,
    val textColor: Color
) {
    PENDING(
        apiValue  = "PENDING",
        label     = "Pendiente",
        dot       = Color(0xFFAB8BF5),
        bgColor   = Color(0xFFAB8BF5),
        textColor = Color(0xFF5B21B6)
    ),
    REJECTED(
        apiValue  = "REJECTED",
        label     = "En proceso",
        dot       = Color(0xFF7C3AED),
        bgColor   = Color(0xFF7C3AED),
        textColor = Color(0xFF3B0764)
    ),
    APPROVED(
        apiValue  = "APPROVED",
        label     = "Resuelto",
        dot       = Color(0xFF4C1D95),
        bgColor   = Color(0xFF4C1D95),
        textColor = Color(0xFFEDE9FE)
    );

    companion object {
        fun from(value: String?): ReportStatus =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: PENDING
    }
}

// ── PANTALLA ──────────────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ──────────────────────────────────
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
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint     = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text       = "Mis Reportes",
                            color      = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                        Text(
                            text     = "Historial de reportes realizados",
                            color    = Color.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentPurple)
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error ?: "", color = Color.Black)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier            = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding      = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.reports) { report ->
                            ReportCard(report)
                        }
                    }
                }
            }
        }
    }
}

// ── CARD ──────────────────────────────────────────────────────────────────────
@Composable
private fun ReportCard(report: Report) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(listOf(AccentPurpleLight, AccentPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                    Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text       = report.description,
                        color      = Color.Black.copy(alpha = 0.7f),
                        fontSize   = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    StatusChip(report.status)
                    Text(
                        text     = DateFormatter.formatDate(report.reportDate),
                        color    = Color.Black.copy(alpha = 0.55f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ── CHIP DE ESTADO ────────────────────────────────────────────────────────────
@Composable
private fun StatusChip(status: String?) {
    val s = ReportStatus.from(status)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(s.bgColor.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(s.dot)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text       = s.label,
                color      = s.textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 11.sp
            )
        }
    }
}