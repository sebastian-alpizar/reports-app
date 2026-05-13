
package com.example.mobile.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
import com.example.mobile.presentation.utils.UiEvent
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch

private val DrawerBg      = Color(0xFF1A1A2E)
private val DrawerAccent  = Color(0xFF6750A4)
private val DrawerSurface = Color(0xFF16213E)

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
                    // TODO: navController.navigate("history")
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
                                Icon(Icons.Default.MyLocation, "Mi ubicación", tint = DrawerAccent)
                            }
                            FloatingActionButton(
                                onClick = { viewModel.toggleReportModal(true) },
                                containerColor = DrawerAccent,
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
                                    color = Color.White
                                )
                            }

                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopStart)
                                    .clip(CircleShape)
                                    .background(DrawerBg.copy(alpha = 0.8f))
                                    .size(48.dp)
                            ) {
                                Icon(Icons.Default.Menu, "Menú", tint = Color.White)
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
                                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                                    Text("Conceder permiso")
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
private fun AppDrawerContent(
    userName: String,
    userEmail: String,
    onHistorial: () -> Unit,
    onPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = DrawerBg,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DrawerSurface)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(DrawerAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(userName,  color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(userEmail, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
        }

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.History, null, tint = Color.White.copy(alpha = 0.7f)) },
            label = { Text("Historial", color = Color.White.copy(alpha = 0.8f)) },
            selected = false,
            onClick = onHistorial,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.7f)) },
            label = { Text("Perfil", color = Color.White.copy(alpha = 0.8f)) },
            selected = false,
            onClick = onPerfil,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
        )

        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color(0xFFEF5350)) },
            label = { Text("Cerrar sesión", color = Color(0xFFEF5350), fontWeight = FontWeight.Medium) },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color(0xFFEF5350).copy(alpha = 0.08f)
            )
        )
        Spacer(Modifier.height(16.dp))
    }
}