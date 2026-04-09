package com.example.mobile.presentation.utils

sealed class UiEvent {

    data class ShowSnackbar(
        val message: String,
        val isError: Boolean = false
    ) : UiEvent()

    object NavigateHome : UiEvent()

}