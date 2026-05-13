package com.example.mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobile.presentation.admin.AdminScreen
import com.example.mobile.presentation.auth.login.LoginScreen
import com.example.mobile.presentation.auth.register.RegisterScreen
import com.example.mobile.presentation.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }


        composable("register") {
            RegisterScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
<<<<<<< Updated upstream
        }

        composable("admin") {
          //  AdminScreen(navController) // ← pasa navController
=======
>>>>>>> Stashed changes
        }

        composable("admin") {
            AdminScreen(navController)
        }

        // TODO: agregar cuando estén listas
        // composable("history") { HistoryScreen(navController) }
        // composable("profile") { ProfileScreen(navController) }
    }
}