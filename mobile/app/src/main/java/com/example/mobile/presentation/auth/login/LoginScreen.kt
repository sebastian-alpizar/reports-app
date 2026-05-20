package com.example.mobile.presentation.auth.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val snackbarState = remember { SnackbarState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarState.show(event.message, event.isError)
                }
                UiEvent.NavigateHome -> {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }

                UiEvent.NavigateLogin -> {  // ← agrega esto
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }

                UiEvent.NavigateAdmin -> {
                    navController.navigate("admin") {
                        popUpTo(0) { inclusive = true }
                    }
                }

            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            viewModel = viewModel,
            navController = navController,
        )
        AppSnackbar(
            message = snackbarState.message,
            isError = snackbarState.isError,
            visible = snackbarState.isVisible,
            onDismiss = {
                scope.launch {
                    snackbarState.dismiss()
                }
            }
        )
    }
}