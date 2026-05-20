package com.example.mobile.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.model.ReportStatus
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.launch

private val AdminPrimary    = Color(0xFF1A1A2E)
private val AdminAccent     = Color(0xFF6750A4)
private val CardBg          = Color(0xFF16213E)
private val PendingColor    = Color(0xFFFFA726)
private val InProgressColor = Color(0xFF42A5F5)
private val ResolvedColor   = Color(0xFF66BB6A)

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val snackbarState = remember { SnackbarState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarState.show(event.message, event.isError)
                UiEvent.NavigateLogin -> {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AdminPrimary)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().background(CardBg).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = AdminAccent, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Panel Municipal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Gestión de reportes ciudadanos", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }

            val reports = uiState.reports ?: emptyList()

            val pending = reports.count { it.status == "PENDING" }
            val inProgress = reports.count { it.status == "IN_PROGRESS" }
            val resolved = reports.count { it.status == "RESOLVED" }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatChip("Pendientes", pending,    PendingColor,    Modifier.weight(1f))
                StatChip("En proceso", inProgress, InProgressColor, Modifier.weight(1f))
                StatChip("Resueltos",  resolved,   ResolvedColor,   Modifier.weight(1f))
            }

            // Search
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Buscar por descripción o zona...", color = Color.White.copy(alpha = 0.4f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = AdminAccent) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, null, tint = Color.White.copy(alpha = 0.6f))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AdminAccent, unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = AdminAccent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter chips
            val filters = listOf(
                null to "Todos",
                ReportStatus.PENDING     to "Pendientes",
                ReportStatus.IN_PROGRESS to "En proceso",
                ReportStatus.RESOLVED    to "Resueltos"
            )
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { (status, label) ->
                    val selected = uiState.selectedStatus == status
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.filterByStatus(status) },
                        label = { Text(label, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AdminAccent, selectedLabelColor = Color.White,
                            containerColor = CardBg, labelColor = Color.White.copy(alpha = 0.7f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true, selected = selected,
                            selectedBorderColor = AdminAccent, borderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            // List
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminAccent)
                    uiState.error != null -> Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFEF5350), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(uiState.error, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = viewModel::loadReports, colors = ButtonDefaults.buttonColors(containerColor = AdminAccent)) { Text("Reintentar") }
                    }
                    uiState.filteredReports.isEmpty() -> Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Inbox, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("No hay reportes", color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)
                    }
                    else -> LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredReports, key = { it.id }) { report ->
                            ReportAdminCard(report = report, onStatusChange = { viewModel.updateStatus(report.id, it) })
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }

        AppSnackbar(
            message = snackbarState.message, isError = snackbarState.isError,
            visible = snackbarState.isVisible, onDismiss = { scope.launch { snackbarState.dismiss() } },
           // modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.15f)).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$count", color = color, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(label, color = color.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun ReportAdminCard(report: ReportResponse, onStatusChange: (ReportStatus) -> Unit) {
    val statusColor = when (report.status) {
        "PENDING"     -> PendingColor
        "IN_PROGRESS" -> InProgressColor
        else          -> ResolvedColor
    }
    val statusLabel = when (report.status) {
        "PENDING"     -> "Pendiente"
        "IN_PROGRESS" -> "En proceso"
        else          -> "Resuelto"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(report.category, color = AdminAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(report.description, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp, maxLines = 2)
                }
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusColor.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(statusLabel, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(report.approximateLocation, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            Text("Cambiar estado:", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (report.status != "PENDING")     StatusButton("Pendiente",  PendingColor)    { onStatusChange(ReportStatus.PENDING) }
                if (report.status != "IN_PROGRESS") StatusButton("En proceso", InProgressColor) { onStatusChange(ReportStatus.IN_PROGRESS) }
                if (report.status != "RESOLVED")    StatusButton("Resuelto",   ResolvedColor)   { onStatusChange(ReportStatus.RESOLVED) }
            }
        }
    }
}

@Composable
private fun StatusButton(label: String, color: Color, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp)
    }
}