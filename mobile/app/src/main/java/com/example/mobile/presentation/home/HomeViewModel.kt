package com.example.mobile.presentation.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.usecase.SendReportUseCase
import com.example.mobile.domain.usecase.location.GetCurrentLocationUseCase
import com.example.mobile.domain.usecase.location.GetLocationUpdatesUseCase
import com.example.mobile.domain.usecase.location.GetNearbyReportsUseCase
import com.example.mobile.domain.usecase.location.HasLocationPermissionUseCase
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentLocation: Location? = null,
    val nearbyReports: List<Report> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingReports: Boolean = false,
    val isSendingReport: Boolean = false,
    val selectedReport: Report? = null,
    val showReportModal: Boolean = false,
    val locationPermissionGranted: Boolean = false,
    val isTrackingLocation: Boolean = false,
    val shouldRequestPermission: Boolean = false
)

data class ReportFormState(
    var description: String = "",
    var selectedImageUri: String? = null,
    val isSubmitting: Boolean = false,
    val descriptionError: String? = null,
    val imageError: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase,
    private val getNearbyReportsUseCase: GetNearbyReportsUseCase,
    private val sendReportUseCase: SendReportUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

//    var uiState by mutableStateOf(HomeUiState())
//        private set

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

//    private val _event = MutableSharedFlow<UiEvent>()
//    val event = _event.asSharedFlow()

    private var locationJob: Job? = null
    // Radio en km para buscar reportes cercanos
    private val nearbyRadiusKm = 5.0

    init {
        checkPermissionAndStart()
    }

    fun checkPermissionAndStart() {
        val hasPermission = hasLocationPermissionUseCase()
        _uiState.value = _uiState.value.copy(locationPermissionGranted = hasPermission)
        if (hasPermission) {
            startLocationTracking()
        }
    }

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(locationPermissionGranted = true)
        startLocationTracking()
    }

    private fun startLocationTracking() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            // Primera ubicación rápida
            val initial = getCurrentLocationUseCase()
            initial?.let { loc ->
                _uiState.value = _uiState.value.copy(currentLocation = loc)
                loadNearbyReports(loc.latitude, loc.longitude)
            }

            // Seguimiento continuo
            _uiState.value = _uiState.value.copy(isTrackingLocation = true)
            getLocationUpdatesUseCase()
                .catch { /* ignorar errores de flujo silenciosamente */ }
                .collect { loc ->
                    val prev = _uiState.value.currentLocation
                    _uiState.value = _uiState.value.copy(currentLocation = loc)

                    // Recargar reportes cada ~200 m de desplazamiento
                    if (prev == null || distanceMeters(prev, loc) > 200) {
                        loadNearbyReports(loc.latitude, loc.longitude)
                    }
                }
        }
    }

    fun loadNearbyReports(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReports = true)
            val result = getNearbyReportsUseCase(latitude, longitude, nearbyRadiusKm)
            result.onSuccess { reports ->
                _uiState.value = _uiState.value.copy(
                    nearbyReports = reports,
                    isLoadingReports = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingReports = false)
            }
        }
    }

    fun onReportMarkerClicked(report: Report) {
        _uiState.value = _uiState.value.copy(selectedReport = report)
    }

    fun onDismissReportDetail() {
        _uiState.value = _uiState.value.copy(selectedReport = null)
    }

    fun onShowReportModal() {
        _uiState.value = _uiState.value.copy(showReportModal = true)
    }

    fun onDismissReportModal() {
        _uiState.value = _uiState.value.copy(showReportModal = false)
    }

    fun onSendReport(context: Context, description: String, imageUri: String?) {
        val location = _uiState.value.currentLocation ?: run {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("No se pudo obtener tu ubicación"))
            }
            return
        }

        if (description.isBlank()) {
            viewModelScope.launch {
                _events.send(UiEvent.ShowSnackbar("La descripción no puede estar vacía"))
            }
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingReport = true)

            val report = Report(
                location = location,
                description = description,
                imageUri = imageUri
            )

            val result = sendReportUseCase(context, report)

            result.onSuccess { savedReport ->
                // Agregar inmediatamente el reporte a la lista local
                val updated = _uiState.value.nearbyReports.toMutableList()
                updated.add(0, savedReport)
                _uiState.value = _uiState.value.copy(
                    nearbyReports = updated,
                    isSendingReport = false,
                    showReportModal = false
                )
                _events.send(UiEvent.ShowSnackbar("✅ Reporte enviado correctamente"))
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSendingReport = false)
                _events.send(UiEvent.ShowSnackbar("❌ Error: ${error.message}"))
            }
        }
    }


    fun onLogout() {
        tokenManager.clear()
        viewModelScope.launch {
            _events.send(UiEvent.Navigate("login"))
        }
    }


    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }

    // Fórmula de Haversine simplificada (approx. metros)
    private fun distanceMeters(a: Location, b: Location): Double {
        val dx = (b.longitude - a.longitude) * 111_320 * Math.cos(Math.toRadians(a.latitude))
        val dy = (b.latitude - a.latitude) * 110_540
        return Math.sqrt(dx * dx + dy * dy)
    }

    var reportFormState by mutableStateOf(ReportFormState())
        private set


    var shouldCenterMap by mutableStateOf(false)
        private set

    fun centerOnCurrentLocation() {
        shouldCenterMap = true
    }

    fun onMapCentered() {
        shouldCenterMap = false
    }

//    init {
//        checkPermissionAndStartUpdates()
//    }

//    private fun checkPermissionAndStartUpdates() {
//        viewModelScope.launch {
//            if (hasLocationPermissionUseCase()) {
//                startLocationUpdates()
//            } else {
//                uiState = uiState.copy(
//                    shouldRequestPermission = true,
//                    isLoading = false
//                )
//            }
//        }
//    }

//    private fun startLocationUpdates() {
//        viewModelScope.launch {
//            getLocationUpdatesUseCase()
//                .catch { exception ->
//                    _event.emit(UiEvent.ShowSnackbar(
//                        "Error al obtener ubicación: ${exception.message}",
//                        true
//                    ))
//                    uiState = uiState.copy(isLoading = false)
//                }
//                .collect { location ->
//                    uiState = uiState.copy(
//                        currentLocation = location,
//                        isLoading = false
//                    )
//                }
//        }
//    }

    fun updateReportDescription(description: String) {
        reportFormState = reportFormState.copy(
            description = description,
        )
    }

    fun updateSelectedImage(uri: String?) {
        reportFormState = reportFormState.copy(selectedImageUri = uri)
    }

//    fun toggleReportModal(show: Boolean) {
//        if (!show) {
//            // Limpiar formulario al cerrar
//            reportFormState = ReportFormState()
//        }
//        uiState = uiState.copy(showReportModal = show)
//    }

//    fun sendReport(context: Context) {
//        val currentLocation = uiState.currentLocation
//        val description = reportFormState.description
//        val imageUri = reportFormState.selectedImageUri
//
//        if (currentLocation == null) {
//            viewModelScope.launch {
//                _event.emit(UiEvent.ShowSnackbar(
//                    "No se ha obtenido la ubicación aún",
//                    true
//                ))
//            }
//            return
//        }
//
//        viewModelScope.launch {
//            reportFormState = reportFormState.copy(isSubmitting = true)
//
//            val report = Report(
//                location = currentLocation,
//                description = description,
//                imageUri = imageUri,
//            )
//
//            println("========== REPORT OBJECT ==========")
//            println("REPORT latitude: ${report.location.latitude}")
//            println("REPORT longitude: ${report.location.longitude}")
//            println("REPORT description: ${report.description}")
//            println("REPORT imageUri: ${report.imageUri}")
//            println("REPORT createdAt: ${report.createdAt}")
//            println("===================================")
//
//            val result = sendReportUseCase(context, report)
//            reportFormState = reportFormState.copy(isSubmitting = false)
//
//            result.fold(
//                onSuccess = {
//                    _event.emit(UiEvent.ReportSubmitted)
//                    toggleReportModal(false)
//                },
//                onFailure = { error ->
//                    _event.emit(UiEvent.ShowSnackbar(
//                        "Error al enviar reporte: ${error.message}",
//                        true
//                    ))
//                }
//            )
//        }
//    }

//    fun onPermissionGranted() {
//        uiState = uiState.copy(shouldRequestPermission = false)
//        startLocationUpdates()
//    }

//    fun onPermissionDenied() {
//        uiState = uiState.copy(shouldRequestPermission = false)
//        viewModelScope.launch {
//            _event.emit(UiEvent.ShowSnackbar(
//                "Permiso de ubicación necesario para mostrar el mapa",
//                true
//            ))
//        }
//    }
}