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
import com.example.mobile.presentation.components.AppDrawerContent
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
// private val TextPrimary       = Color(0xFF1A0533)
// private val TextSecondary     = Color(0xFF1A0533).copy(alpha = 0.6f)

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
            AppDrawerContent (
                userName  = uiState.userName,
                userEmail = uiState.userEmail,
                onHistorial = {
                    scope.launch { drawerState.close()
                    }
                    navController.navigate("history")
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
                                        .background(Color.White.copy(alpha = 0.8f))
                                        .size(48.dp)
                                ) {
                                    Icon(Icons.Default.Menu, "Menú", tint = AccentPurpleLight)
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
                                Text("Se necesita permiso de ubicación", color = Color.White)
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
