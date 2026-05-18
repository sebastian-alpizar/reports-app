package com.example.mobile.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.mobile.presentation.components.ReportDetailCard
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobile.presentation.components.ReportMapView
import com.example.mobile.presentation.components.ReportModal
import com.example.mobile.presentation.utils.UiEvent
import android.Manifest
import androidx.compose.material.icons.filled.MyLocation
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context          = LocalContext.current
    val uiState          by viewModel.uiState.collectAsState()
    val reportFormState  = viewModel.reportFormState
    val snackbarState    = remember { SnackbarState() }
    val modalSnackbar    = remember { SnackbarState() }
    val scope            = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onLocationPermissionGranted()
    }

    LaunchedEffect(uiState.locationPermissionGranted) {
        if (!uiState.locationPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Eventos del ViewModel
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Navigate     -> if (event.route == "login") onNavigateToLogin()
                else                    -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState.currentLocation != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // FAB: centrar en mi ubicación
                    FloatingActionButton(
                        onClick        = { viewModel.centerOnCurrentLocation() },
                        containerColor = Color.White.copy(alpha = 0.9f),
                        modifier       = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint               = Color(0xFF7C3AED)
                        )
                    }

                    // FAB: crear reporte
                    FloatingActionButton(
                        onClick        = { viewModel.onShowReportModal() },
                        containerColor = Color(0xFF7C3AED),
                        modifier       = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.CameraAlt,
                            contentDescription = "Reportar accidente",
                            tint               = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mapa
            ReportMapView(
                currentLocation = uiState.currentLocation,
                nearbyReports   = uiState.nearbyReports,
                shouldCenterMap = viewModel.shouldCenterMap,
                onMapCentered   = { viewModel.onMapCentered() },
                onReportClicked = { report -> viewModel.onReportMarkerClicked(report) },
                modifier        = Modifier.fillMaxSize()
            )

            // Loading inicial
            if (uiState.isLoading && uiState.currentLocation == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = Color(0xFF7C3AED)
                )
            }

            // Badge reportes cercanos — abajo izquierda
            NearbyReportsBadge(
                reportCount = uiState.nearbyReports.size,
                isLoading   = uiState.isLoadingReports,
                onClick     = {
                    uiState.nearbyReports.firstOrNull()
                        ?.let { viewModel.onReportMarkerClicked(it) }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 90.dp)
            )

            // Detalle de reporte seleccionado
            ReportDetailCard(
                report    = uiState.selectedReport,
                onDismiss = { viewModel.onDismissReportDetail() },
                modifier  = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )

            // Snackbar global
            AppSnackbar(
                message   = snackbarState.message,
                isError   = snackbarState.isError,
                visible   = snackbarState.isVisible,
                onDismiss = { scope.launch { snackbarState.dismiss() } }
            )
        }
    }

    // Modal de reporte
    if (uiState.showReportModal) {
        ReportModal(
            currentLocation     = uiState.currentLocation,
            isSubmitting        = uiState.isSendingReport,
            snackbarState       = modalSnackbar,
            reportFormState     = reportFormState,
            onDescriptionChange = { viewModel.updateReportDescription(it) },
            onImageSelected     = { viewModel.updateSelectedImage(it) },
            onSubmit = {
                viewModel.onSendReport(
                    context     = context,
                    description = reportFormState.description,
                    imageUri    = reportFormState.selectedImageUri
                )
            },
            onDismiss = { viewModel.onDismissReportModal() }
        )
    }


//    // Handle permission request from ViewModel
//    LaunchedEffect(uiState.shouldRequestPermission) {
//        if (uiState.shouldRequestPermission && !locationPermissionState.status.isGranted) {
//            locationPermissionState.launchPermissionRequest()
//        }
//    }
//
//    // Handle permission result
//    LaunchedEffect(locationPermissionState.status.isGranted) {
//        if (locationPermissionState.status.isGranted) {
//            viewModel.onPermissionGranted()
//        } else if (!locationPermissionState.status.isGranted &&
//            !locationPermissionState.status.shouldShowRationale) {
//            viewModel.onPermissionDenied()
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        floatingActionButton = {
//            if (locationPermissionState.status.isGranted && uiState.currentLocation != null) {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    horizontalAlignment = Alignment.End
//                ) {
//
//                    // FAB: volver a mi ubicación
//                    FloatingActionButton(
//                        onClick = {
//                            viewModel.centerOnCurrentLocation()
//                        },
//                        containerColor = Color.White.copy(alpha = 0.8f),
//                        modifier = Modifier.size(56.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.MyLocation,
//                            contentDescription = "Mi ubicación",
//                            tint = Color(0xFF6750A4)
//                        )
//                    }
//
//                    // FAB: crear reporte
//                    FloatingActionButton(
//                        onClick = {
//                            viewModel.toggleReportModal(true)
//                        },
//                        containerColor = Color(0xFF6750A4),
//                        modifier = Modifier.size(56.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.CameraAlt,
//                            contentDescription = "Reportar accidente",
//                            tint = Color.White.copy(alpha = 0.8f)
//                        )
//                    }
//                }
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            when {
//                locationPermissionState.status.isGranted -> {
//                    // Mostrar mapa si hay permiso
//                    ReportMapView(
//                        currentLocation = uiState.currentLocation,
//                        shouldCenterMap = viewModel.shouldCenterMap,
//                        onMapCentered = { viewModel.onMapCentered() },
//                        modifier = Modifier.fillMaxSize(),
//                        onMapReady = { }
//                    )
//
//                    // Mostrar loading mientras se obtiene la primera ubicación
//                    if (uiState.isLoading) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.align(Alignment.Center),
//                            color = Color.White
//                        )
//                    }
//
//                    // Mostrar modal de reporte
//                    if (uiState.showReportModal && uiState.currentLocation != null) {
//                        ReportModal(
//                            currentLocation = uiState.currentLocation,
//                            description = reportFormState.description,
//                            selectedImageUri = reportFormState.selectedImageUri,
//                            isSubmitting = reportFormState.isSubmitting,
//                            onDescriptionChange = viewModel::updateReportDescription,
//                            onImageSelected = viewModel::updateSelectedImage,
//                            onSubmit = { viewModel.sendReport(context) },
//                            onDismiss = { viewModel.toggleReportModal(false) }
//                        )
//                    }
//                }
//                else -> {
//                    // Mostrar mensaje de permisos
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(32.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Se necesita permiso de ubicación",
//                            color = Color.White,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//
//                        Button(
//                            onClick = {
//                                locationPermissionState.launchPermissionRequest()
//                            }
//                        ) {
//                            Text("Conceder permiso")
//                        }
//                    }
//                }
//            }
//        }
//    }

}

