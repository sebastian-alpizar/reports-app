
package com.example.mobile.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val reportApi: ReportApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val id = tokenManager.getUserId()
                    ?: throw Exception("No se encontró el ID del usuario")
                val user = reportApi.getUserById(id)
                uiState = uiState.copy(user = user, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}