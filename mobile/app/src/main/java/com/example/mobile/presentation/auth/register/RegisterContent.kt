package com.example.mobile.presentation.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel,
    navController: NavController
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.formState.nameError != null
        )

        viewModel.formState.nameError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.formState.emailError != null
        )

        viewModel.formState.emailError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.nationalId,
            onValueChange = { viewModel.nationalId = it },
            label = { Text("Cédula") },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.formState.nationalIdError != null
        )

        viewModel.formState.nationalIdError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
            isError = viewModel.formState.passwordError != null,

            trailingIcon = {
                val icon =
                    if (passwordVisible)
                        Icons.Filled.VisibilityOff
                    else
                        Icons.Filled.Visibility
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(icon, null)
                }
            }
        )

        viewModel.formState.passwordError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("¿Ya tienes cuenta? ")
            Text(
                text = "Inicia sesión",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
    }
}