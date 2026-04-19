package com.example.mobile.presentation.home

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
import com.google.accompanist.permissions.*
import androidx.compose.material.icons.filled.MyLocation

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState = viewModel.uiState  // Sin by, es directo
    val reportFormState = viewModel.reportFormState
    val context = LocalContext.current

    // Permission state
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.ReportSubmitted -> {
                    snackbarHostState.showSnackbar("Reporte enviado exitosamente")
                }
                else -> {}
            }
        }
    }

    // Handle permission request from ViewModel
    LaunchedEffect(uiState.shouldRequestPermission) {
        if (uiState.shouldRequestPermission && !locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Handle permission result
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            viewModel.onPermissionGranted()
        } else if (!locationPermissionState.status.isGranted &&
            !locationPermissionState.status.shouldShowRationale) {
            viewModel.onPermissionDenied()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (locationPermissionState.status.isGranted && uiState.currentLocation != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    // FAB: volver a mi ubicación
                    FloatingActionButton(
                        onClick = {
                            viewModel.centerOnCurrentLocation()
                        },
                        containerColor = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Mi ubicación",
                            tint = Color(0xFF6750A4)
                        )
                    }

                    // FAB: crear reporte
                    FloatingActionButton(
                        onClick = {
                            viewModel.toggleReportModal(true)
                        },
                        containerColor = Color(0xFF6750A4),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Reportar accidente",
                            tint = Color.White.copy(alpha = 0.8f)
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
            when {
                locationPermissionState.status.isGranted -> {
                    // Mostrar mapa si hay permiso
                    ReportMapView(
                        currentLocation = uiState.currentLocation,
                        shouldCenterMap = viewModel.shouldCenterMap,
                        onMapCentered = { viewModel.onMapCentered() },
                        modifier = Modifier.fillMaxSize(),
                        onMapReady = { }
                    )

                    // Mostrar loading mientras se obtiene la primera ubicación
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    // Mostrar modal de reporte
                    if (uiState.showReportModal && uiState.currentLocation != null) {
                        ReportModal(
                            currentLocation = uiState.currentLocation,
                            description = reportFormState.description,
                            selectedImageUri = reportFormState.selectedImageUri,
                            isSubmitting = reportFormState.isSubmitting,
                            onDescriptionChange = viewModel::updateReportDescription,
                            onImageSelected = viewModel::updateSelectedImage,
                            onSubmit = { viewModel.sendReport(context) },
                            onDismiss = { viewModel.toggleReportModal(false) }
                        )
                    }
                }
                else -> {
                    // Mostrar mensaje de permisos
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Se necesita permiso de ubicación",
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            onClick = {
                                locationPermissionState.launchPermissionRequest()
                            }
                        ) {
                            Text("Conceder permiso")
                        }
                    }
                }
            }
        }
    }
}