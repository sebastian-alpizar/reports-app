package com.example.mobile.presentation.history
import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.presentation.components.AppBackground

private val AccentPurple = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg = Color.White.copy(alpha = 0.5f)

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppBackground {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ── TOP BAR ──────────────────────────────────────────────
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

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

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
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {

                        Text(
                            text = "Mis Reportes",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Historial de reportes realizados",
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            when {

                uiState.isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = AccentPurple
                        )
                    }
                }

                uiState.error != null -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = uiState.error ?: "",
                            color = Color.Black
                        )
                    }
                }

                else -> {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
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

@Composable
private fun ReportCard(
    report: ReportResponse
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(16.dp)
    ) {

        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    AccentPurpleLight,
                                    AccentPurple
                                )
                            )
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
                        text = report.description,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(
                color = Color.Black.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                //StatusChip(report.status)

                Text(
                    text = report.reportDate,
                    color = Color.Black.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: String
) {

    val color = when (status.lowercase()) {
        "pendiente" -> Color(0xFFFF9800)
        "resuelto" -> Color(0xFF4CAF50)
        else -> AccentPurple
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {

        Text(
            text = status,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp
        )
    }
}