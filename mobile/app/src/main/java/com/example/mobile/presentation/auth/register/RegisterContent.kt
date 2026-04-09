package com.example.mobile.presentation.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile.presentation.components.*
import com.example.mobile.presentation.utils.GlassModifiers

@Composable
fun RegisterContent(
    viewModel: RegisterViewModel,
    navController: NavController
) {

    AppBackground {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .clip(RoundedCornerShape(32.dp))
                .then(GlassModifiers.glassCard())
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Crear cuenta",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = "Nombre",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.nameError != null
            )

            viewModel.formState.nameError?.let {
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.emailError != null
            )

            viewModel.formState.emailError?.let {
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = viewModel.nationalId,
                onValueChange = { viewModel.nationalId = it },
                label = "Cédula",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.nationalIdError != null
            )

            viewModel.formState.nationalIdError?.let {
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppPasswordField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.passwordError != null
            )

            viewModel.formState.passwordError?.let {
                Text(it, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(28.dp))

            AppButton(
                text = "Registrarse",
                isLoading = viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.register()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(
                    "¿Ya tienes cuenta? ",
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    "Inicia sesión",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF),
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}