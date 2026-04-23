package com.example.mobile.presentation.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.usecase.SendReportUseCase
import com.example.mobile.domain.usecase.location.GetLocationUpdatesUseCase
import com.example.mobile.domain.usecase.location.HasLocationPermissionUseCase
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentLocation: Location? = null,
    val isLoading: Boolean = true,
    val shouldRequestPermission: Boolean = false,
    val showReportModal: Boolean = false
)

data class ReportFormState(
    var description: String = "",
    var selectedImageUri: String? = null,
    val isSubmitting: Boolean = false,
    // errores
    val descriptionError: String? = null,
    val imageError: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase,
    private val sendReportUseCase: SendReportUseCase
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    var reportFormState by mutableStateOf(ReportFormState())
        private set

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    var shouldCenterMap by mutableStateOf(false)
        private set

    fun centerOnCurrentLocation() {
        shouldCenterMap = true
    }

    fun onMapCentered() {
        shouldCenterMap = false
    }

    init {
        checkPermissionAndStartUpdates()
    }

    private fun checkPermissionAndStartUpdates() {
        viewModelScope.launch {
            if (hasLocationPermissionUseCase()) {
                startLocationUpdates()
            } else {
                uiState = uiState.copy(
                    shouldRequestPermission = true,
                    isLoading = false
                )
            }
        }
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            getLocationUpdatesUseCase()
                .catch { exception ->
                    _event.emit(UiEvent.ShowSnackbar(
                        "Error al obtener ubicación: ${exception.message}",
                        true
                    ))
                    uiState = uiState.copy(isLoading = false)
                }
                .collect { location ->
                    uiState = uiState.copy(
                        currentLocation = location,
                        isLoading = false
                    )
                }
        }
    }

    fun toggleReportModal(show: Boolean) {
        if (!show) {
            // Limpiar formulario al cerrar
            reportFormState = ReportFormState()
        }
        uiState = uiState.copy(showReportModal = show)
    }

    fun sendReport(context: Context) {
        if (!validateReportForm()) return

        val currentLocation = uiState.currentLocation
        val description = reportFormState.description
        val imageUri = reportFormState.selectedImageUri

        if (currentLocation == null) {
            viewModelScope.launch {
                _event.emit(UiEvent.ShowSnackbar(
                    "No se ha obtenido la ubicación aún",
                    true
                ))
            }
            return
        }

        viewModelScope.launch {
            reportFormState = reportFormState.copy(isSubmitting = true)

            val report = Report(
                location = currentLocation,
                description = description,
                imageUri = imageUri
            )

            println("========== REPORT OBJECT ==========")
            println("REPORT latitude: ${report.location.latitude}")
            println("REPORT longitude: ${report.location.longitude}")
            println("REPORT description: ${report.description}")
            println("REPORT imageUri: ${report.imageUri}")
            println("REPORT createdAt: ${report.createdAt}")
            println("===================================")

            val result = sendReportUseCase(context, report)
            reportFormState = reportFormState.copy(isSubmitting = false)

            println("REPORT response: ${result}")



            result.fold(
                onSuccess = {
                    _event.emit(UiEvent.ShowSnackbar(
                        "Reporte enviado exitosamente",
                        false
                    ))
                    toggleReportModal(false)
                },
                onFailure = { error ->
                    _event.emit(UiEvent.ShowSnackbar(
                        "Error al enviar reporte: ${error.message}",
                        true
                    ))
                }
            )
        }
    }

    fun onPermissionGranted() {
        uiState = uiState.copy(shouldRequestPermission = false)
        startLocationUpdates()
    }

    fun onPermissionDenied() {
        uiState = uiState.copy(shouldRequestPermission = false)
        viewModelScope.launch {
            _event.emit(UiEvent.ShowSnackbar(
                "Permiso de ubicación necesario para mostrar el mapa",
                true
            ))
        }
    }

    fun updateReportDescription(description: String) {
        reportFormState = reportFormState.copy(
            description = description,
            descriptionError = null
        )
    }

    fun updateSelectedImage(uri: String?) {
        reportFormState = reportFormState.copy(
            selectedImageUri = uri,
            imageError = null
        )
    }

    private fun validateReportForm(): Boolean {
        val descriptionError =
            if (reportFormState.description.isBlank())
                "La descripción es obligatoria"
            else null

        val imageError =
            if (reportFormState.selectedImageUri.isNullOrBlank())
                "Debes seleccionar una imagen"
            else null

        reportFormState = reportFormState.copy(
            descriptionError = descriptionError,
            imageError = imageError
        )

        return descriptionError == null && imageError == null
    }
}