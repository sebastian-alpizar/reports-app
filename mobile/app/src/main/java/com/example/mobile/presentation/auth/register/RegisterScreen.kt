package com.example.mobile.presentation.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.utils.UiEvent

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                UiEvent.NavigateHome -> {
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                }
                UiEvent.ReportSubmitted -> {
                    // No hace nada aquí porque este evento
                    // pertenece a HomeScreen, pero Kotlin lo exige
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        RegisterContent(
            viewModel = viewModel,
            navController = navController
        )
    }
}