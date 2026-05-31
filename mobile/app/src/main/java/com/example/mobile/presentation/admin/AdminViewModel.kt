package com.example.mobile.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.UpdateStatusRequest
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.usecase.DeleteReportUseCase
import com.example.mobile.domain.usecase.GetAllReportsUseCase
import com.example.mobile.domain.usecase.UpdateReportStatusUseCase
import com.example.mobile.domain.usecase.UpdateReportUseCase
import com.example.mobile.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isLoading: Boolean = false,
    val reports: List<Report> = emptyList(),
    val error: String? = null,
    val updatingId: Long? = null,
    val deletingId: Long? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase,
    private val deleteReportUseCase: DeleteReportUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    init {
        loadAllReports()
    }

    fun loadAllReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // val reports = reportApi.getAllReports()
                val reports = getAllReportsUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = reports
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error del servidor"
                )
            }
        }
    }

    fun updateStatus(reportId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(updatingId = reportId)

//                reportApi.updateReportStatus(
//                    reportId,
//                    UpdateStatusRequest(status = newStatus)
//                )

                updateReportStatusUseCase(
                    reportId,
                    UpdateStatusRequest(status = newStatus)
                )

                _uiState.value = _uiState.value.copy(
                    reports = _uiState.value.reports.map { report ->
                        if (report.id == reportId) report.copy(status = newStatus)
                        else report
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al actualizar"
                )
            } finally {
                _uiState.value = _uiState.value.copy(updatingId = null)
            }
        }
    }

    fun deleteReport(reportId: Long) {
        _uiState.value = _uiState.value.copy(deletingId = reportId)

        viewModelScope.launch {
            deleteReportUseCase(reportId)
                .onSuccess {
                    // Elimina localmente sin recargar toda la lista
                    _uiState.value = _uiState.value.copy(
                        reports = _uiState.value.reports.filter { it.id != reportId }
                    )
                    _event.emit(UiEvent.ShowSnackbar("Reporte eliminado", false))
                }
                .onFailure { error ->
                    _event.emit(UiEvent.ShowSnackbar(error.message ?: "Error al eliminar", true))
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Error al eliminar"
                    )
                }

            _uiState.value = _uiState.value.copy(deletingId = null)
        }

//        viewModelScope.launch {
//            try {
//                _uiState.value = _uiState.value.copy(deletingId = reportId)
//
//                //reportApi.deleteReport(reportId)
//                deleteReportUseCase(reportId)
//
//                // Elimina localmente sin recargar toda la lista
//                _uiState.value = _uiState.value.copy(
//                    reports = _uiState.value.reports.filter { it.id != reportId }
//                )
//
//            } catch (e: Exception) {
//                _uiState.value = _uiState.value.copy(
//                    error = e.message ?: "Error al eliminar"
//                )
//            } finally {
//                _uiState.value = _uiState.value.copy(deletingId = null)
//            }
//        }
    }
}