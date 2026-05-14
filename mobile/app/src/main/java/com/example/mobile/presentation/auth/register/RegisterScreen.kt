package com.example.mobile.presentation.auth.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.example.mobile.presentation.auth.login.LoginContent
//import com.example.mobile.presentation.components.snackbar.AppSnackbar
//import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
//    navController: NavController,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()

//    LaunchedEffect(true) {
//        viewModel.event.collect { event ->
//            when (event) {
//                is UiEvent.ShowSnackbar -> {
//                    snackbarState.show(event.message, event.isError)
//                }
//                UiEvent.NavigateHome -> {
//                    navController.navigate("home") {
//                        popUpTo("register") { inclusive = true }
//                    }
//                }
//            }
//        }
//    }

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                UiEvent.NavigateHome   -> onRegisterSuccess()
                else                   -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues  ->
        RegisterContent(
            viewModel = viewModel,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }



//Box(modifier = Modifier.fillMaxSize()) {
//        RegisterContent(
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