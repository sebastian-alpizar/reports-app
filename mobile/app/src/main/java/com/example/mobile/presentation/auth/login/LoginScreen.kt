package com.example.mobile.presentation.auth.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobile.presentation.utils.UiEvent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
//    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    //    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        viewModel.event.collect { event ->

//            when (event) {
//                is UiEvent.ShowSnackbar -> {
//                    snackbarState.show(event.message, event.isError)
//                }
//                UiEvent.NavigateHome -> {
//                    navController.navigate("home") {
//                        popUpTo("login") { inclusive = true }
//                    }
//
//                }
//            }
//        }
//    }
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                UiEvent.NavigateHome   -> onLoginSuccess()
                else                   -> Unit
            }
        }
    }

    Scaffold( snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LoginContent(
            viewModel = viewModel,
            onNavigateToRegister = onNavigateToRegister,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        LoginContent(
//            viewModel = viewModel,
//            navController = navController,
//        )
//        AppSnackbar(
//            message = snackbarState.message,
//            isError = snackbarState.isError,
//            visible = snackbarState.isVisible,
//            onDismiss = {
//                scope.launch {
//                    snackbarState.dismiss()
//                }
//            }
//        )
//    }
}