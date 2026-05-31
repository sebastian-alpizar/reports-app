package com.example.mobile.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.usecase.GetReportsByUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = false,
    val reports: List<Report> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getReportsByUserUseCase: GetReportsByUserUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val userId = tokenManager.getUserId()

                if (userId == -1L || userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró el usuario logueado"
                    )
                    return@launch
                }

                val reports = getReportsByUserUseCase(userId)

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
}