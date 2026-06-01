package com.example.mobile.presentation.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.presentation.components.AppBackground
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.utils.DateFormatter
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// ── COLORES ───────────────────────────────────────────────────────────────────
private val AccentPurple      = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg            = Color.White.copy(alpha = 0.52f)

// ── ENUM DE ESTADOS ───────────────────────────────────────────────────────────
private enum class ReportStatus(
    val apiValue : String,
    val label    : String,
    val desc     : String,
    val dot      : Color,
    val bgColor  : Color,
    val textColor: Color
) {
    PENDING(
        apiValue  = "PENDING",
        label     = "Pendiente",
        desc      = "Sin atender aún",
        dot       = Color(0xFFAB8BF5),
        bgColor   = Color(0xFFAB8BF5),
        textColor = Color(0xFF5B21B6)
    ),
    REJECTED(
        apiValue  = "REJECTED",
        label     = "En proceso",
        desc      = "Se está atendiendo",
        dot       = Color(0xFF7C3AED),
        bgColor   = Color(0xFF7C3AED),
        textColor = Color(0xFF3B0764)
    ),
    APPROVED(
        apiValue  = "APPROVED",
        label     = "Resuelto",
        desc      = "Caso cerrado",
        dot       = Color(0xFF4C1D95),
        bgColor   = Color(0xFF4C1D95),
        textColor = Color(0xFFEDE9FE)
    );

    companion object {
        fun from(value: String?): ReportStatus =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) } ?: PENDING
    }
}

// ── PANTALLA PRINCIPAL ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val snackbarState = remember { SnackbarState() }
    val scope = rememberCoroutineScope()

    val uiState        by viewModel.uiState.collectAsState()
    var searchQuery    by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<ReportStatus?>(null) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarState.show(event.message, event.isError)
                else -> {}
            }
        }
    }

    val filteredReports = remember(uiState.reports, searchQuery, selectedFilter) {
        uiState.reports
            .let { list ->
                if (selectedFilter != null)
                    list.filter { ReportStatus.from(it.status) == selectedFilter }
                else list
            }
            .let { list ->
                if (searchQuery.isBlank()) list
                else list.filter { report ->
                    report.description.contains(searchQuery, ignoreCase = true) ||
                            report.userName?.contains(searchQuery, ignoreCase = true) == true ||
                            report.userEmail?.contains(searchQuery, ignoreCase = true) == true ||
                            report.approximateLocation?.contains(searchQuery, ignoreCase = true) == true ||
                            report.category?.contains(searchQuery, ignoreCase = true) == true
                }
            }
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TOP BAR ───────────────────────────────────
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
                    modifier          = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            navController.navigate("home") {
                                launchSingleTop = true
                                popUpTo("home")
                            }
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
                                tint     = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = "Panel de Administración",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF1E1B4B)
                        )
                        Text(
                            text     = "${filteredReports.size} de ${uiState.reports.size} reportes",
                            fontSize = 11.sp,
                            color    = Color.Black.copy(alpha = 0.45f)
                        )
                    }
                }
            }

            // ── CONTENIDO ─────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
                    }
                }

//                uiState.error != null -> {
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text( "Error al cargar reportes", color = Color.Black)
//                    }
//                }

                else -> {
                    LazyColumn(
                        modifier            = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding      = PaddingValues(bottom = 24.dp)
                    ) {

                        // ── BARRA DE BÚSQUEDA ─────────────
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardBg)
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint     = AccentPurple.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    BasicTextField(
                                        value         = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine    = true,
                                        modifier      = Modifier.weight(1f),
                                        textStyle     = TextStyle(
                                            fontSize = 13.sp,
                                            color    = Color.Black.copy(alpha = 0.8f)
                                        ),
                                        decorationBox = { inner ->
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    "Buscar por descripción, usuario",
                                                    fontSize = 13.sp,
                                                    color    = Color.Black.copy(alpha = 0.35f)
                                                )
                                            }
                                            inner()
                                        }
                                    )
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick  = { searchQuery = "" },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Limpiar",
                                                tint     = Color.Black.copy(alpha = 0.4f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // ── CHIPS DE FILTRO ───────────────
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding        = PaddingValues(horizontal = 0.dp)
                            ) {
                                item {
                                    val isSelected = selectedFilter == null
                                    Surface(
                                        onClick = { selectedFilter = null },
                                        shape   = RoundedCornerShape(50.dp),
                                        color   = Color.Transparent,
                                        border  = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.dp else 0.5.dp,
                                            color = if (isSelected) AccentPurple.copy(alpha = 0.8f)
                                            else Color.White.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Row(
                                            modifier          = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text       = "Todos",
                                                fontSize   = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color      = if (isSelected) AccentPurple
                                                else Color.White.copy(alpha = 0.7f)
                                            )
                                            if (uiState.reports.isNotEmpty()) {
                                                Spacer(Modifier.width(5.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50.dp))
                                                        .background(
                                                            if (isSelected) AccentPurple.copy(alpha = 0.12f)
                                                            else Color.White.copy(alpha = 0.12f)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text       = "${uiState.reports.size}",
                                                        fontSize   = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color      = if (isSelected) AccentPurple
                                                        else Color.White.copy(alpha = 0.5f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                items(ReportStatus.entries) { statusOption ->
                                    val isSelected = selectedFilter == statusOption
                                    val count = uiState.reports.count {
                                        ReportStatus.from(it.status) == statusOption
                                    }
                                    Surface(
                                        onClick = {
                                            selectedFilter = if (isSelected) null else statusOption
                                        },
                                        shape  = RoundedCornerShape(50.dp),
                                        color  = Color.Transparent,
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 1.dp else 0.5.dp,
                                            color = if (isSelected) statusOption.bgColor.copy(alpha = 0.8f)
                                            else Color.White.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Row(
                                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(statusOption.dot)
                                            )
                                            Spacer(Modifier.width(6.dp))
                                            Text(
                                                text       = statusOption.label,
                                                fontSize   = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                color      = if (isSelected) statusOption.dot
                                                else Color.White.copy(alpha = 0.7f)
                                            )
                                            if (count > 0) {
                                                Spacer(Modifier.width(5.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50.dp))
                                                        .background(
                                                            if (isSelected) statusOption.bgColor.copy(alpha = 0.15f)
                                                            else Color.White.copy(alpha = 0.12f)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text       = "$count",
                                                        fontSize   = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color      = if (isSelected) statusOption.dot
                                                        else Color.White.copy(alpha = 0.5f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── LISTA VACÍA ───────────────────
                        if (filteredReports.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        Icon(
                                            Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(70.dp)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "Sin resultados",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        } else {
                            items(filteredReports) { report ->
                                AdminReportCard(
                                    report         = report,
                                    isLoading      = uiState.updatingId == report.id,
                                    onStatusChange = { status -> viewModel.updateStatus(report.id, status) },
                                    onDelete       = { viewModel.deleteReport(report.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        AppSnackbar(
            message = snackbarState.message,
            isError = snackbarState.isError,
            visible = snackbarState.isVisible,
            onDismiss = {
                scope.launch { snackbarState.dismiss() }
            }
        )
    }
}

// ── CARD DE REPORTE ───────────────────────────────────────────────────────────
@Composable
fun AdminReportCard(
    report        : ReportResponse,
    isLoading     : Boolean,
    onStatusChange: (String) -> Unit,
    onDelete      : () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showDialog       by remember { mutableStateOf(false) }
    var detailExpanded   by remember { mutableStateOf(false) }

    val status = ReportStatus.from(report.status)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .padding(16.dp)
    ) {
        Column {

            // ── ICONO + DESCRIPCIÓN + BOTÓN ELIMINAR ──────
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
                        tint     = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = report.description,
                        fontSize   = 13.sp,
                        color      = Color.Black.copy(alpha = 0.72f),
                        lineHeight = 19.sp
                    )
                    report.userName?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text       = it,
                            fontSize   = 11.sp,
                            color      = AccentPurple.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // ── BOTÓN ELIMINAR (arriba a la derecha) ──
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick  = { showDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint     = Color(0xFFEF4444),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // ── IMAGEN SIEMPRE VISIBLE ────────────────────
            report.photoUrl?.let { url ->
                Spacer(Modifier.height(12.dp))
                AsyncImage(
                    model              = url,
                    contentDescription = "Foto del reporte",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            Spacer(Modifier.height(13.dp))
            HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
            Spacer(Modifier.height(11.dp))

            // ── FOOTER: ESTADO + FECHA + EXPANDIR ────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    Surface(
                        onClick = { dropdownExpanded = true },
                        shape   = RoundedCornerShape(50.dp),
                        color   = status.bgColor.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(status.dot)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text       = status.label,
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = status.textColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint     = status.textColor,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded         = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        shape            = RoundedCornerShape(16.dp),
                        containerColor   = Color.White,
                        shadowElevation  = 8.dp,
                        modifier         = Modifier.width(180.dp)
                    ) {
                        ReportStatus.entries.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(option.dot)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text       = option.label,
                                                fontSize   = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color      = Color.Black
                                            )
                                            Text(
                                                text     = option.desc,
                                                fontSize = 11.sp,
                                                color    = Color.Black.copy(alpha = 0.45f)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    dropdownExpanded = false
                                    onStatusChange(option.apiValue)
                                }
                            )
                            if (index < ReportStatus.entries.size - 1) {
                                HorizontalDivider(
                                    modifier  = Modifier.padding(horizontal = 16.dp),
                                    color     = Color.Black.copy(alpha = 0.07f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint     = Color.Black.copy(alpha = 0.4f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text     = DateFormatter.formatDate(report.reportDate),
                        fontSize = 11.sp,
                        color    = Color.Black.copy(alpha = 0.42f)
                    )

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick  = { detailExpanded = !detailExpanded },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                if (detailExpanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Ver detalle",
                                tint     = AccentPurple,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // ── DETALLE EXPANDIBLE ────────────────────────
            AnimatedVisibility(
                visible = detailExpanded,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                    Spacer(Modifier.height(12.dp))

                    report.userName?.let            { DetailRow(Icons.Default.Person,      "Usuario",     it) }
                    report.userEmail?.let           { DetailRow(Icons.Default.Email,        "Correo",      it) }
                    report.approximateLocation?.let { DetailRow(Icons.Default.LocationOn,   "Ubicación",   it) }
                    if (report.latitude != null && report.longitude != null) {
                        DetailRow(Icons.Default.MyLocation, "Coordenadas", "${report.latitude}, ${report.longitude}")
                    }
                    report.category?.let { DetailRow(Icons.Default.Category, "Categoría", it) }
                }
            }

            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(
                    color       = Color.White.copy(alpha = 0.8f),
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Eliminar reporte",
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1E1B4B)
                )
            },
            text = {
                Text(
                    "¿Seguro que querés eliminar este reporte? Esta acción no se puede deshacer.",
                    fontSize = 13.sp,
                    color    = Color.Black.copy(alpha = 0.6f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape  = RoundedCornerShape(10.dp)
                ) {
                    Text("Eliminar", color = Color.White, fontSize = 13.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false },
                    shape   = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar", fontSize = 13.sp)
                }
            }
        )
    }
}

// ── FILA DE DETALLE ───────────────────────────────────────────────────────────
@Composable
private fun DetailRow(
    icon : androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = AccentPurple.copy(alpha = 0.7f),
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text     = label,
            fontSize = 11.sp,
            color    = Color.Black.copy(alpha = 0.45f),
            modifier = Modifier.width(80.dp)
        )
        Text(
            text       = value,
            fontSize   = 12.sp,
            color      = Color.Black.copy(alpha = 0.75f),
            fontWeight = FontWeight.Medium
        )
    }
}