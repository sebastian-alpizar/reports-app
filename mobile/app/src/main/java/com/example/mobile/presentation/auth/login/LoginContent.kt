package com.example.mobile.presentation.auth.login

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile.presentation.components.AppBackground
import com.example.mobile.presentation.components.AppButton
import com.example.mobile.presentation.components.AppPasswordField
import com.example.mobile.presentation.components.AppTextField
import com.example.mobile.presentation.components.*
import com.example.mobile.presentation.utils.GlassModifiers

@Composable
fun LoginContent(
    viewModel: LoginViewModel,
//    navController: NavController
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier

) {
    AppBackground(modifier = modifier) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ReportApp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reporta accidentes en tiempo real",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

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
                text = "Login",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
//                value = viewModel.formState.email,
                value = viewModel.formState.email,
//                onValueChange = viewModel::updateEmail,
                onValueChange = {viewModel.updateEmail(it)},
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.emailError != null
            )

            viewModel.formState.emailError?.let {
                Text(it, color = Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AppPasswordField(
//                value = viewModel.formState.password,
                value = viewModel.formState.password,
//                onValueChange = viewModel::updatePassword,
                onValueChange = {viewModel.updatePassword(it)},
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.formState.passwordError != null
            )

            viewModel.formState.passwordError?.let {
                Text(it, color = Color.Red,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            AppButton(
                text = "Iniciar sesión",
                isLoading = viewModel.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.login()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Text(
                    "¿Aún no tienes cuenta? ",
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    text = "Regístrate",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF80D8FF),
                    modifier = Modifier.clickable {
//                        navController.navigate("register")
                        onNavigateToRegister()
                    }
                )
            }
        }
    }
}