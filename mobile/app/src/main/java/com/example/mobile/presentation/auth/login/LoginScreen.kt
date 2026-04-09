package com.example.mobile.presentation.auth.login

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.utils.UiEvent

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.event.collect { event ->

            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        event.message
                    )
                }
                UiEvent.NavigateHome -> {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }

                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        LoginContent(
            viewModel = viewModel,
            navController = navController
        )
    }
}