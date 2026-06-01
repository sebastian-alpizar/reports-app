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
import com.example.mobile.data.remote.dto.CreateReportDto
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.usecase.DeleteReportUseCase
import com.example.mobile.domain.usecase.GetNearbyReportsUseCase
import com.example.mobile.domain.usecase.SendReportUseCase
import com.example.mobile.domain.usecase.UpdateReportUseCase
import com.example.mobile.domain.usecase.VoteReportUseCase
import com.example.mobile.domain.usecase.location.GetLocationUpdatesUseCase
import com.example.mobile.domain.usecase.location.HasLocationPermissionUseCase
import com.example.mobile.domain.usecase.location.ReverseGeocodeUseCase
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentLocation: Location? = null,
    val currentAddress: String? = null,
    val isLoading: Boolean = true,
    val shouldRequestPermission: Boolean = false,
    val showReportModal: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val isAdmin: Boolean = false,
    val nearbyReports: List<Report> = emptyList(),
    val isLoadingReports: Boolean = false,
    val selectedReport: Report? = null,
    val editingReport: Report? = null
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
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase,
    private val sendReportUseCase: SendReportUseCase,
    private val updateReportUseCase: UpdateReportUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
    private val getNearbyReportsUseCase: GetNearbyReportsUseCase,
    private val tokenManager: TokenManager,
    private val voteReportUseCase: VoteReportUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    var reportFormState by mutableStateOf(ReportFormState())
        private set

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    var shouldCenterMap by mutableStateOf(false)
        private set

    val currentUserId: Long? get() = tokenManager.getUserId()

    fun centerOnCurrentLocation() { shouldCenterMap = true }
    fun onMapCentered() { shouldCenterMap = false }

    init {
        checkPermissionAndStartUpdates()
        uiState = uiState.copy(
            userName  = tokenManager.getUserName()  ?: "Usuario",
            userEmail = tokenManager.getUserEmail() ?: "",
            isAdmin = tokenManager.isAdmin(),
        )
    }

    private fun checkPermissionAndStartUpdates() {
        viewModelScope.launch {
            if (hasLocationPermissionUseCase()) startLocationUpdates()
            else uiState = uiState.copy(shouldRequestPermission = true, isLoading = false)
        }
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            getLocationUpdatesUseCase()
                .catch { exception ->
                    _event.emit(UiEvent.ShowSnackbar("Error al obtener ubicación: ${exception.message}", true))
                    uiState = uiState.copy(isLoading = false)
                }
                .collect { location ->
                    val prev = uiState.currentLocation
                    uiState = uiState.copy(currentLocation = location, isLoading = false)

                    if (prev == null) {
                        shouldCenterMap = true
                    }

                    if (prev == null || distanceMeters(prev, location) > 200) {
                        loadNearbyReports(location.latitude, location.longitude)
                    }
                }
        }
    }

    fun loadCurrentAddress(context: Context) {
        val location = uiState.currentLocation ?: return
        viewModelScope.launch {
            val address = reverseGeocodeUseCase(
                context = context,
                latitude = location.latitude,
                longitude = location.longitude
            )
            uiState = uiState.copy(currentAddress = address)
        }
    }


    fun loadNearbyReports(latitude: Double?, longitude: Double?) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingReports = true)
            getNearbyReportsUseCase(latitude, longitude, 0.5)
                .onSuccess { reports ->
                    val updatedSelectedReport =
                        uiState.selectedReport?.let { selected ->
                            reports.find { it.id == selected.id }
                        }
                    uiState = uiState.copy(
                        nearbyReports = reports,
                        selectedReport = updatedSelectedReport,
                        isLoadingReports = false
                    )
                }
                .onFailure {
                    uiState = uiState.copy(isLoadingReports = false)
                }
        }
    }

    fun onReportMarkerClicked(report: Report) {
        uiState = uiState.copy(selectedReport = report)
    }

    fun onDismissReportDetail() {
        uiState = uiState.copy(selectedReport = null)
    }

    fun onEditReport(report: Report) {
        reportFormState = ReportFormState(description = report.description)
        uiState = uiState.copy(editingReport = report, showReportModal = true)
    }


    fun toggleReportModal(show: Boolean) {
        if (!show) reportFormState = ReportFormState()
        uiState = uiState.copy(showReportModal = show)
    }

    fun sendReport(context: Context) {
        val editing = uiState.editingReport
        if (editing != null) {
            updateExistingReport(context, editing)
        } else {
            createNewReport(context)
        }
    }

    private fun createNewReport(context: Context) {
        if (!validateReportForm()) return
        val currentLocation = uiState.currentLocation ?: run {
            viewModelScope.launch {
                _event.emit(UiEvent.ShowSnackbar("No se ha obtenido la ubicación aún", true))
            }
            return
        }
        viewModelScope.launch {
            reportFormState = reportFormState.copy(isSubmitting = true)
            // val report = CreateReportDto(

            val approximateLocation = reverseGeocodeUseCase(
                context   = context,
                latitude  = currentLocation.latitude,
                longitude = currentLocation.longitude
            )
            val report = CreateReportDto(
                location    = currentLocation,
                description = reportFormState.description,
                imageUri    = reportFormState.selectedImageUri,
                approximateLocation = approximateLocation
            )
            val result = sendReportUseCase(context, report)
            println("RESULTADO: $result")
            reportFormState = reportFormState.copy(isSubmitting = false)
            result.fold(
                onSuccess = {
                    _event.emit(UiEvent.ShowSnackbar("Reporte enviado exitosamente", false))
                    toggleReportModal(false)
                    uiState.currentLocation?.let { loadNearbyReports(it.latitude, it.longitude) }
                },
                onFailure = { error ->
                    _event.emit(UiEvent.ShowSnackbar(error.message ?: "Error al crear reporte", true))
                }
            )
        }
    }

    private fun updateExistingReport(context: Context, report: Report) {
        if (reportFormState.description.isBlank()) {
            viewModelScope.launch {
                _event.emit(UiEvent.ShowSnackbar("La descripción no puede estar vacía", true))
            }
            return
        }
        viewModelScope.launch {
            reportFormState = reportFormState.copy(isSubmitting = true)
            val result = updateReportUseCase(
                context     = context,
                reportId    = report.id,
                description = reportFormState.description,
                imageUri    = reportFormState.selectedImageUri
            )
            reportFormState = reportFormState.copy(isSubmitting = false)
            result.fold(
                onSuccess = {
                    _event.emit(UiEvent.ShowSnackbar("Reporte actualizado exitosamente", false))
                    toggleReportModal(false)
                    //uiState.currentLocation?.let { loadNearbyReports(it.latitude, it.longitude) }
                },
                onFailure = { error ->
                    _event.emit(UiEvent.ShowSnackbar("Error al actualizar: ${error.message}", true))
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
            _event.emit(UiEvent.ShowSnackbar("Permiso de ubicación necesario para mostrar el mapa", true))
        }
    }

    fun updateReportDescription(description: String) {
        reportFormState = reportFormState.copy(description = description, descriptionError = null)
    }

    fun updateSelectedImage(uri: String?) {
        reportFormState = reportFormState.copy(selectedImageUri = uri, imageError = null)
    }

    private fun validateReportForm(): Boolean {
        val descriptionError = if (reportFormState.description.isBlank()) "La descripción es obligatoria" else null
        val imageError = if (reportFormState.selectedImageUri.isNullOrBlank()) "Debes seleccionar una imagen" else null
        reportFormState = reportFormState.copy(descriptionError = descriptionError, imageError = imageError)
        return descriptionError == null && imageError == null
    }

    fun logout() {
        tokenManager.clear()
        viewModelScope.launch { _event.emit(UiEvent.NavigateLogin) }
    }

    private fun distanceMeters(a: Location, b: Location): Double {
        val dx = (b.longitude!! - a.longitude!!) * 111_320 * Math.cos(Math.toRadians(a.latitude!!))
        val dy = (b.latitude!! - a.latitude) * 110_540
        return Math.sqrt(dx * dx + dy * dy)
    }

    fun voteReport(report: Report) {

         if (report.userHasVoted) return

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {

            voteReportUseCase(report.id)
                .onSuccess { message ->
//                    _event.emit(
//                        UiEvent.ShowSnackbar(message, false)
//                    )
                    uiState.currentLocation?.let {
                        loadNearbyReports(
                            it.latitude,
                            it.longitude
                        )
                    }
                }
                .onFailure { error ->
                    _event.emit(
                        UiEvent.ShowSnackbar(error.message ?: "Error al votar", true
                        )
                    )
                }
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun onDeleteReport(report: Report) {
        viewModelScope.launch {
            deleteReportUseCase(report.id)
                .onSuccess {
                    android.util.Log.d("HomeVM", "Reporte eliminado, recargando mapa...")  // ← aquí
                    uiState = uiState.copy(selectedReport = null)
                    _event.emit(UiEvent.ShowSnackbar("Reporte eliminado", false))
                    uiState.currentLocation?.let { loadNearbyReports(it.latitude, it.longitude) }
                }
                .onFailure { error ->
                    _event.emit(UiEvent.ShowSnackbar(error.message ?: "Error al eliminar", true))
                }
        }
    }
    fun refreshReports() {
        uiState.currentLocation?.let { loadNearbyReports(it.latitude, it.longitude) }
    }
}