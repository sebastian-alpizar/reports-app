package com.example.mobile.presentation.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.domain.model.Notification
import com.example.mobile.domain.usecase.DeleteNotificationUseCase
import com.example.mobile.domain.usecase.GetNotificationsUseCase
import com.example.mobile.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(NotificationsUiState())
        private set

    private val _event = MutableSharedFlow<UiEvent>()

    init {
        loadNotifications()
    }

    fun loadNotifications() {

        viewModelScope.launch {

            uiState = uiState.copy(isLoading = true)
            val userId = tokenManager.getUserId()
            if (userId == -1L || userId == null) {
                _event.emit(
                    UiEvent.ShowSnackbar(
                        "No se encontró el usuario logueado",
                        true
                    )
                )
                uiState = uiState.copy(isLoading = false)
                return@launch
            }
            val result = getNotificationsUseCase(userId)

            println(result)

            result.onSuccess { notifications ->
                uiState = uiState.copy(
                    notifications = notifications,
                    isLoading = false
                )
            }

            result.onFailure { exception ->
                uiState = uiState.copy(isLoading = false)
                _event.emit(
                    UiEvent.ShowSnackbar(
                        exception.message ?: "No se pudo obtener las notificaciones",
                        true
                    )
                )
            }
        }
    }

    fun removeNotification(id: String) {
        viewModelScope.launch {
            val result = deleteNotificationUseCase(id)

            println(result)

            result.onSuccess {
                uiState = uiState.copy(
                    notifications = uiState.notifications.filterNot {
                        it.id == id
                    }
                )
            }

            result.onFailure { exception ->
                uiState = uiState.copy(isLoading = false,)
                _event.emit(
                    UiEvent.ShowSnackbar(
                        exception.message ?: "Error eliminando la notificación",
                        true
                    )
                )
            }
        }
    }
}