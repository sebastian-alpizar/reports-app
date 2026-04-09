package com.example.mobile.presentation.auth.register

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.usecase.RegisterUseCase
import com.example.mobile.presentation.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mobile.domain.validation.RegisterValidator

data class RegisterFormState(
    val nameError: String? = null,
    val emailError: String? = null,
    val nationalIdError: String? = null,
    val passwordError: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val registerValidator: RegisterValidator
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    var formState by mutableStateOf(RegisterFormState())

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    fun register() {
        if (!validateForm()) return

        formState = RegisterFormState()

        viewModelScope.launch {
            isLoading = true
            val result = registerUseCase(
                name,
                email,
                nationalId,
                password
            )
            isLoading = false
            result.fold(
                onSuccess = {
                    _event.emit(
                        UiEvent.ShowSnackbar(
                            "Usuario creado correctamente"
                        )
                    )
                    _event.emit(UiEvent.NavigateHome)
                },
                onFailure = {
                    _event.emit(
                        UiEvent.ShowSnackbar(
                            it.message ?: "Error al registrarse",
                            true
                        )
                    )
                }
            )
        }
    }

    private fun validateForm(): Boolean {
        val validation = registerValidator.validate(
            name,
            email,
            nationalId,
            password
        )

        formState = RegisterFormState()

        if (validation != null) {
            formState = when (validation) {
                "El nombre es obligatorio" ->
                    formState.copy(nameError = validation)

                "La cédula es obligatoria" ->
                    formState.copy(nationalIdError = validation)

                "El email es obligatorio",
                "Email inválido" ->
                    formState.copy(emailError = validation)

                "La contraseña debe tener al menos 6 caracteres" ->
                    formState.copy(passwordError = validation)

                else -> formState
            }
            return false
        }
        return true
    }
}