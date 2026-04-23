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
    val passwordError: String? = null,

    var name: String = "",
    var email: String = "",
    var nationalId: String = "",
    var password: String = ""
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val registerValidator: RegisterValidator
) : ViewModel() {
    var isLoading by mutableStateOf(false)

    var formState by mutableStateOf(RegisterFormState())

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    fun updateName(name: String) {
        formState = formState.copy(
            name = name,
            nameError = null
        )
    }

    fun updateEmail(email: String) {
        formState = formState.copy(
            email = email,
            emailError = null
        )
    }

    fun updateNationalId(nationalId: String) {
        formState = formState.copy(
            nationalId = nationalId,
            nationalIdError = null
        )
    }

    fun updatePassword(password: String) {
        formState = formState.copy(
            password = password,
            passwordError = null
        )
    }

    fun register() {
        if (!validateForm()) return

        viewModelScope.launch {
            isLoading = true
            val result = registerUseCase(
                formState.name,
                formState.email,
                formState.nationalId,
                formState.password
            )
            isLoading = false
            result.fold(
                onSuccess = {
                    _event.emit(
                        UiEvent.ShowSnackbar(
                            "Usuario creado correctamente",
                            false
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
            formState.name,
            formState.email,
            formState.nationalId,
            formState.password
        )

        formState = formState.copy(
            nameError = null,
            emailError = null,
            nationalIdError = null,
            passwordError = null
        )

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