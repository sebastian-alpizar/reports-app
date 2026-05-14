package com.example.mobile.presentation.utils

sealed class UiEvent {

//    val route: Any

    data class ShowSnackbar(
        val message: String,
        val isError: Boolean = false
    ) : UiEvent()

    data object NavigateHome : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data object ReportSubmitted : UiEvent()
}