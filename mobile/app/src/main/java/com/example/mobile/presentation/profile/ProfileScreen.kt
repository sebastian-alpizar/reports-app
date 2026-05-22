package com.example.mobile.presentation.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.components.AppBackground


// ── Paleta ──────────────────────────────────────────────────────────────────
private val AccentPurple    = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg            = Color.White.copy(alpha = 0.5f)


//private val TextPrimary     = Color(0xFF1A0533)
//private val TextSecondary   = Color(0xFF1A0533).copy(alpha = 0.55f)
private val TagBg           = Color(0xFF7C3AED).copy(alpha = 0.25f)

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 10.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBg)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Mi Perfil",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 0.2.sp
                        )
                        Text(
                            "Información de tu cuenta",
                            color = Color.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            // ── Estados ──────────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = AccentPurpleLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                null,
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(14.dp))
                            Text(
                                uiState.error,
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                uiState.user != null -> {
                    val user = uiState.user

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 10.dp)
                            .padding(top = 4.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // ── Avatar card ──────────────────────────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(CardBg)
                                .padding(vertical = 32.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Avatar con anillo gradiente
                                Box(contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(98.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(AccentPurpleLight, AccentPurple, Color(0xFF4F1FC7))
                                                )
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .background(AccentPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 38.sp
                                        )
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

                                Text(
                                    user.name,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    letterSpacing = 0.2.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    user.email,
                                    color = Color.Black,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.2.sp
                                )
                                Spacer(Modifier.height(16.dp))

                                // Badge rol
//                                Box(
//                                    modifier = Modifier
//                                        .clip(RoundedCornerShape(50.dp))
//                                        .background(TagBg)
//                                        .padding(horizontal = 18.dp, vertical = 7.dp)
//                                ) {
//                                    Row(
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        horizontalArrangement = Arrangement.Center
//                                    ) {
//                                        Icon(
//                                            if (user.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
//                                            null,
//                                            tint = Color.White,
//                                            modifier = Modifier.size(15.dp)
//                                        )
//                                        Spacer(Modifier.width(7.dp))
//                                        Text(
//                                            if (user.isAdmin) "Administrador" else "Ciudadano",
//                                            color = Color.Black,
//                                            fontSize = 13.sp,
//                                            fontWeight = FontWeight.SemiBold,
//                                            letterSpacing = 0.3.sp
//                                        )
//                                    }
//                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        // ── Sección label ────────────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AccentPurpleLight)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "INFORMACIÓN PERSONAL",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.8.sp
                            )
                        }

                        // ── Info cards ───────────────────────────────────────
                        ProfileInfoCard(Icons.Default.Person,             "Nombre completo",    user.name)
                        ProfileInfoCard(Icons.Default.Email,              "Correo electrónico", user.email)
//                        ProfileInfoCard(Icons.Default.Badge,              "ID de usuario",      "#${user.id}")
                        ProfileInfoCard(
                            Icons.Default.AdminPanelSettings,
                            "Rol en el sistema",
                            if (user.isAdmin) "Administrador" else "Ciudadano"
                        )

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoCard(icon: ImageVector, label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AccentPurple.copy(alpha = 0.5f), AccentPurple.copy(alpha = 0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            // Texto — ocupa el espacio restante
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    color = Color.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.4.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    value,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.1.sp
                )
            }

            // Chevron decorativo
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}