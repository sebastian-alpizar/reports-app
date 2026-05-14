package com.example.mobile.presentation.auth.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mobile.core.util.TokenManager
import com.example.mobile.domain.validation.EmailValidator
import com.example.mobile.domain.validation.PasswordValidator
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginFormState(
    val emailError: String? = null,
    val passwordError: String? = null,
    var email: String = "",
    var password: String = "",
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator
) : ViewModel() {
    // Form state
    var formState by mutableStateOf(LoginFormState())

    // Estados
    var isLoading by mutableStateOf(false)
    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    fun updateEmail(email: String) {
        formState = formState.copy(
            email = email,
            emailError = null
        )
    }

    fun updatePassword(password: String) {
        formState = formState.copy(
            password = password,
            passwordError = null
        )
    }

    fun login() {
        if (!validateForm()) return

        viewModelScope.launch {
            isLoading = true
            val result = loginUseCase(formState.email, formState.password)
            isLoading = false

            result.fold(
                onSuccess = { token ->
                    tokenManager.saveToken(token)
                    _event.emit(UiEvent.NavigateHome)
                },
                onFailure = {
                    _event.emit(
                        UiEvent.ShowSnackbar(
                            it.message ?: "Error al iniciar sesión",
                            true
                        )
                    )
                }
            )
        }
    }

    private fun validateForm(): Boolean {
        val emailValidation = emailValidator.validate(formState.email)
        val passwordValidation = passwordValidator.validate(formState.password)
        formState = formState.copy(
            emailError = emailValidation,
            passwordError = passwordValidation
        )
        return emailValidation == null && passwordValidation == null
    }

    fun logout() {
        tokenManager.clear()
        viewModelScope.launch {
            _event.emit(UiEvent.NavigateLogin)
        }
    }
}