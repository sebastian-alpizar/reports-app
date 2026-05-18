package com.example.mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile.presentation.auth.login.LoginScreen
import com.example.mobile.presentation.auth.register.RegisterScreen
import com.example.mobile.presentation.home.HomeScreen

private object Routes {
    const val LOGIN    = "login"
    const val REGISTER = "register"
    const val HOME     = "home"
}

@Composable
fun NavGraph(startDestination: String = Routes.LOGIN) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess   = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}