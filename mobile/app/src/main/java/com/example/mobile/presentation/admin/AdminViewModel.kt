package com.example.mobile.presentation.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.model.ReportStatus
import com.example.mobile.domain.usecase.GetAllReportsUseCase
import com.example.mobile.domain.usecase.UpdateReportStatusUseCase
import com.example.mobile.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val getAllReportsUseCase: GetAllReportsUseCase,
    private val updateReportStatusUseCase: UpdateReportStatusUseCase
) : ViewModel() {

    var uiState by mutableStateOf(AdminUiState())
        private set

    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val reports = getAllReportsUseCase()
                uiState = uiState.copy(
                    reports = reports,
                    filteredReports = reports,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, error = e.message ?: "Error desconocido")
                _event.send(UiEvent.ShowSnackbar("Error al cargar reportes", isError = true))
            }
        }
    }

    fun updateStatus(reportId: Long, newStatus: ReportStatus) {
        viewModelScope.launch {
            try {
                updateReportStatusUseCase(reportId, newStatus)
                loadReports()
                _event.send(UiEvent.ShowSnackbar("Estado actualizado correctamente", isError = false))
            } catch (e: Exception) {
                _event.send(UiEvent.ShowSnackbar("Error al actualizar estado", isError = true))
            }
        }
    }

    fun filterByStatus(status: ReportStatus?) {
        uiState = uiState.copy(selectedStatus = status)
        applyFilters()
    }

    fun filterByCategory(category: String?) {
        uiState = uiState.copy(selectedCategory = category)
        applyFilters()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
        applyFilters()
    }

    private fun applyFilters() {
        var result = uiState.reports

        uiState.selectedStatus?.let { status ->
            result = result.filter { it.status == status.name }
        }

        uiState.selectedCategory?.let { category ->
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (uiState.searchQuery.isNotBlank()) {
            result = result.filter {
                it.description.contains(uiState.searchQuery, ignoreCase = true) ||
                        it.approximateLocation.contains(uiState.searchQuery, ignoreCase = true)
            }
        }

        uiState = uiState.copy(filteredReports = result)
    }
}