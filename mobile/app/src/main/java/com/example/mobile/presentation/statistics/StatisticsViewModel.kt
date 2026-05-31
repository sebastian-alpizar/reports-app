package com.example.mobile.presentation.statistics

import com.example.mobile.data.remote.dto.StatisticsResponse
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.usecase.GetStatisticsUseCase
import com.example.mobile.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val statistics: StatisticsResponse? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    var uiState by mutableStateOf(StatisticsUiState())
        private set

    private val _event = MutableSharedFlow<UiEvent>()

    init {
        loadStatistics()
    }

    fun loadStatistics() {

        viewModelScope.launch {

            uiState = uiState.copy(
                isLoading = true
            )

            val result = getStatisticsUseCase()

            result.onSuccess { statistics ->

                uiState = uiState.copy(
                    statistics = statistics,
                    isLoading = false
                )
            }

            result.onFailure { exception ->

                uiState = uiState.copy(
                    isLoading = false
                )

                _event.emit(
                    UiEvent.ShowSnackbar(
                        exception.message ?: "No se pudieron cargar las estadísticas",
                        true
                    )
                )
            }
        }
    }
}