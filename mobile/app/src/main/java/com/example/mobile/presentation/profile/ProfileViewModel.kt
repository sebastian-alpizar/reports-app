package com.example.mobile.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.core.util.TokenManager
import com.example.mobile.data.remote.api.UserApi
import com.example.mobile.data.remote.dto.UpdateUserRequest
import com.example.mobile.data.remote.dto.UserDto
import com.example.mobile.domain.validation.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserDto? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveError: String? = null,
    val saveSuccess: Boolean = false,
    val isEditing: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val emailValidator: EmailValidator   // ← reutiliza el que ya existe
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun clearSaveError() {
        uiState = uiState.copy(saveError = null, saveSuccess = false)  // ← agregás saveSuccess = false
    }

    private fun loadProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val id = tokenManager.getUserId()
                    ?: throw Exception("No se encontró el ID del usuario")
                val user = userApi.getUserById(id)
                uiState = uiState.copy(user = user, isLoading = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun toggleEditing() {
        uiState = uiState.copy(
            isEditing = !uiState.isEditing,
            saveSuccess = false,
            saveError = null
        )
    }


    fun updateUser(name: String, email: String, nationalId: String) {
        // ── Validación usando el EmailValidator compartido ─────────────────
        val emailError = emailValidator.validate(email)
        if (emailError != null) {
            uiState = uiState.copy(saveError = emailError, isEditing = true)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, saveError = null)
            try {
                val id = tokenManager.getUserId()
                    ?: throw Exception("No se encontró el ID del usuario")

                userApi.updateUser(
                    id,
                    UpdateUserRequest(name = name, email = email, nationalId = nationalId)
                )

                val updatedUser = userApi.getUserById(id)
                uiState = uiState.copy(
                    user = updatedUser,
                    isSaving = false,
                    isEditing = false,
                    saveSuccess = true,
                    saveError = null
                )

            } catch (e: retrofit2.HttpException) {
                val errorMessage = try {
                    val errorBody = e.response()?.errorBody()?.string()
                    val json = org.json.JSONObject(errorBody ?: "")
                    json.getString("message")
                } catch (ex: Exception) {
                    "Error al actualizar"
                }
                uiState = uiState.copy(
                    isSaving = false,
                    isEditing = true,
                    saveError = errorMessage
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    isEditing = true,
                    saveError = e.message ?: "Error al actualizar"
                )
            }
        }
    }


}