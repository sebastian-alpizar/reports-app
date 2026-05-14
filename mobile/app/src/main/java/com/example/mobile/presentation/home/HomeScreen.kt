package com.example.mobile.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.components.ReportMapView
import com.example.mobile.presentation.components.ReportModal
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.utils.GlassModifiers
import com.example.mobile.presentation.utils.UiEvent
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch

// ── Paleta (igual que el resto del proyecto) ─────────────────────────────────
private val AccentPurple      = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val DangerRed         = Color(0xFFB71C1C)
private val TextPrimary       = Color(0xFF1A0533)
private val TextSecondary     = Color(0xFF1A0533).copy(alpha = 0.6f)

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarState = remember { SnackbarState() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val uiState = viewModel.uiState
    val reportFormState = viewModel.reportFormState
    val context = LocalContext.current

    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

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

    LaunchedEffect(uiState.shouldRequestPermission) {
        if (uiState.shouldRequestPermission && !locationPermissionState.status.isGranted)
            locationPermissionState.launchPermissionRequest()
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) viewModel.onPermissionGranted()
        else if (!locationPermissionState.status.shouldShowRationale) viewModel.onPermissionDenied()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                userName  = uiState.userName,
                userEmail = uiState.userEmail,
                onHistorial = {
                    scope.launch { drawerState.close() }
                },
                onPerfil = {
                    scope.launch { drawerState.close() }
                    navController.navigate("profile")
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    viewModel.logout()
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    if (locationPermissionState.status.isGranted && uiState.currentLocation != null) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            FloatingActionButton(
                                onClick = { viewModel.centerOnCurrentLocation() },
                                containerColor = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(Icons.Default.MyLocation, "Mi ubicación", tint = AccentPurpleLight)
                            }
                            FloatingActionButton(
                                onClick = { viewModel.toggleReportModal(true) },
                                containerColor = AccentPurple,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, "Reportar", tint = Color.White)
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    when {
                        locationPermissionState.status.isGranted -> {
                            ReportMapView(
                                currentLocation = uiState.currentLocation,
                                shouldCenterMap = viewModel.shouldCenterMap,
                                onMapCentered = { viewModel.onMapCentered() },
                                modifier = Modifier.fillMaxSize(),
                                onMapReady = { }
                            )

                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = AccentPurpleLight
                                )
                            }

                            // Botón menú — mismo estilo glass del proyecto
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopStart)
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            ) {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier
                                        .shadow(6.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.85f))
                                        .size(48.dp)
                                ) {
                                    Icon(Icons.Default.Menu, "Menú", tint = Color(0xFF7C3AED))
                                }
                            }

                            if (uiState.showReportModal && uiState.currentLocation != null) {
                                ReportModal(
                                    currentLocation = uiState.currentLocation,
                                    isSubmitting = reportFormState.isSubmitting,
                                    snackbarState = snackbarState,
                                    reportFormState = reportFormState,
                                    onDescriptionChange = viewModel::updateReportDescription,
                                    onImageSelected = viewModel::updateSelectedImage,
                                    onSubmit = { viewModel.sendReport(context) },
                                    onDismiss = { viewModel.toggleReportModal(false) }
                                )
                            } else {
                                AppSnackbar(
                                    message = snackbarState.message,
                                    isError = snackbarState.isError,
                                    visible = snackbarState.isVisible,
                                    onDismiss = { scope.launch { snackbarState.dismiss() } }
                                )
                            }
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Se necesita permiso de ubicación", color = TextPrimary)
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = { locationPermissionState.launchPermissionRequest() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                                ) {
                                    Text("Conceder permiso", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Drawer ────────────────────────────────────────────────────────────────────
@Composable
private fun AppDrawerContent(
    userName: String,
    userEmail: String,
    onHistorial: () -> Unit,
    onPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(290.dp),
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFA78BFA),
                            Color(0xFFC084FC),
                            Color(0xFFF0ABFC)
                        ),
                        radius = 1200f
                    )
                )
        ) {

            // ── Header con avatar ─────────────────────────────────────────────
            Spacer(Modifier.windowInsetsPadding(WindowInsets.statusBars).height(0.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AccentPurpleLight, AccentPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        userName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.2.sp
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        userEmail,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Label sección ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp).height(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AccentPurpleLight)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "MENÚ",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // ── Items de navegación ───────────────────────────────────────────
            DrawerItem(
                icon  = Icons.Default.History,
                label = "Historial",
                onClick = onHistorial
            )
            DrawerItem(
                icon  = Icons.Default.Person,
                label = "Mi Perfil",
                onClick = onPerfil
            )

            Spacer(Modifier.weight(1f))

            // ── Logout ────────────────────────────────────────────────────────
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFF1A0533).copy(alpha = 0.15f)
            )
            Spacer(Modifier.height(4.dp))
            DrawerItem(
                icon       = Icons.AutoMirrored.Filled.Logout,
                label      = "Cerrar sesión",
                iconTint   = DangerRed,
                labelColor = DangerRed,
                onClick    = onLogout
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color = TextPrimary,
    labelColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(
            label,
            color = labelColor,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            letterSpacing = 0.2.sp
        )
    }
}