package com.example.mobile.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.ReportResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val isLoading: Boolean = false,
    val reports: List<ReportResponse> = emptyList(),
    val error: String? = null,
    val updatingId: Long? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val reportApi: ReportApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadAllReports()
    }

    fun loadAllReports() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val reports = reportApi.getAllReports() // 🔥 necesitas este endpoint
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    reports = reports
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun updateStatus(reportId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(updatingId = reportId)

                reportApi.updateReportStatus(
                    reportId,
                    mapOf("status" to newStatus)
                )

                loadAllReports()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al actualizar"
                )
            } finally {
                _uiState.value = _uiState.value.copy(updatingId = null)
            }
        }
    }
}