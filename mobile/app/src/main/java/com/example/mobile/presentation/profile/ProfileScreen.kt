package com.example.mobile.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mobile.presentation.components.AppBackground

// ── Paleta ───────────────────────────────────────────────────────────────────
private val AccentPurple      = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val CardBg            = Color.White.copy(alpha = 0.45f)
private val CardBgSolid       = Color.White.copy(alpha = 0.45f)
private val ErrorRed          = Color(0xFFEF5350)
private val TextPrimary       = Color(0xFF1A1A2E)
private val TextSecondary     = Color(0xFF6B6B8A)

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState           = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    var editingField      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            editingField = null
            snackbarHostState.showSnackbar("Perfil actualizado correctamente")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier       = Modifier.padding(16.dp).clip(RoundedCornerShape(16.dp)),
                    containerColor = AccentPurple,
                    contentColor   = Color.White,
                    action = {
                        TextButton(onClick = { data.dismiss() }) {
                            Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(data.visuals.message, fontSize = 14.sp)
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        AppBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                // ── TOP BAR ──────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.45f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentPurple.copy(alpha = 0.15f))
                                .clickable { navController.popBackStack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = AccentPurple, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Mi Perfil", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Información de tu cuenta", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                // ── CONTENIDO ────────────────────────────────────────────────
                when {
                    uiState.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    uiState.error != null && uiState.user == null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                Box(
                                    modifier = Modifier.size(72.dp).clip(CircleShape).background(ErrorRed.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(36.dp))
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(uiState.error, color = TextPrimary, fontSize = 14.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }

                    uiState.user != null -> {
                        val user = uiState.user

                        var nameField       by remember(user) { mutableStateOf(user.name ?: "") }
                        var emailField      by remember(user) { mutableStateOf(user.email ?: "") }
                        var nationalIdField by remember(user) { mutableStateOf(user.nationalId ?: "") }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                                .padding(top = 4.dp, bottom = 40.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // ── AVATAR CARD ──────────────────────────────────
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
                                    Box(contentAlignment = Alignment.Center) {
                                        Box(
                                            modifier = Modifier
                                                .size(108.dp)
                                                .clip(CircleShape)
                                                .background(Brush.linearGradient(listOf(Color(0xFFE0C3FC), AccentPurpleLight, AccentPurple, Color(0xFF4F1FC7))))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(96.dp)
                                                .clip(CircleShape)
                                                .background(Brush.linearGradient(listOf(AccentPurple, Color(0xFF5B21B6)))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                (user.name ?: "U").firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 40.sp
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(20.dp))
                                    Text(user.name ?: "Sin nombre", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(user.email ?: "Sin correo", color = TextSecondary, fontSize = 13.sp)
                                    Spacer(Modifier.height(16.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(AccentPurple.copy(alpha = 0.12f))
                                            .padding(horizontal = 18.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                if (user.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                                null, tint = AccentPurple, modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(Modifier.width(7.dp))
                                            Text(
                                                if (user.isAdmin) "Administrador" else "Ciudadano",
                                                color = AccentPurple, fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }

                            // ── INFO PERSONAL ────────────────────────────────
                            SectionHeader("INFORMACIÓN PERSONAL")

                            ProfileRowCard(Icons.Default.Person, "Nombre completo", nameField) {
                                viewModel.clearSaveError(); editingField = "name"
                            }
                            ProfileRowCard(Icons.Default.Email, "Correo electrónico", emailField) {
                                viewModel.clearSaveError(); editingField = "email"
                            }
                            ProfileRowCard(Icons.Default.Badge, "Cédula", nationalIdField) {
                                viewModel.clearSaveError(); editingField = "nationalId"
                            }
                        }

                        // ── MODAL DE EDICIÓN ─────────────────────────────────
                        if (editingField != null) {
                            val fieldLabel = when (editingField) {
                                "name"       -> "Nombre completo"
                                "email"      -> "Correo electrónico"
                                "nationalId" -> "Cédula"
                                else         -> ""
                            }
                            val fieldIcon = when (editingField) {
                                "name"       -> Icons.Default.Person
                                "email"      -> Icons.Default.Email
                                "nationalId" -> Icons.Default.Badge
                                else         -> Icons.Default.Edit
                            }
                            val fieldDescription = when (editingField) {
                                "name"       -> "Este nombre es visible en tu perfil público."
                                "email"      -> "Se usa para ingresar a tu cuenta."
                                "nationalId" -> "Este dato está vinculado a tu identidad en la plataforma."
                                else         -> ""
                            }
                            var fieldValue by remember(editingField) {
                                mutableStateOf(
                                    when (editingField) {
                                        "name"       -> nameField
                                        "email"      -> emailField
                                        "nationalId" -> nationalIdField
                                        else         -> ""
                                    }
                                )
                            }

                            Dialog(
                                onDismissRequest = { editingField = null; viewModel.clearSaveError() },
                                properties = DialogProperties(usePlatformDefaultWidth = false)
                            ) {
                                AppBackground {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 22.dp)
                                                .shadow(20.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                                                .clip(RoundedCornerShape(28.dp))
                                                .background(CardBgSolid)
                                        ) {
                                            // ── Encabezado ───────────────────
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 28.dp, bottom = 6.dp, start = 24.dp, end = 24.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(56.dp)
                                                        .clip(CircleShape)
                                                        .background(Brush.linearGradient(listOf(AccentPurpleLight, AccentPurple))),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(fieldIcon, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                                }
                                                Spacer(Modifier.height(14.dp))
                                                Text(fieldLabel.uppercase(), color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.5.sp)
                                                Spacer(Modifier.height(4.dp))
                                                Text(fieldValue.ifBlank { "—" }, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp, textAlign = TextAlign.Center)
                                                Spacer(Modifier.height(6.dp))
                                                Text(fieldDescription, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
                                            }

                                            // ── Input ────────────────────────
                                            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                                OutlinedTextField(
                                                    value         = fieldValue,
                                                    onValueChange = { fieldValue = it },
                                                    modifier      = Modifier.fillMaxWidth(),
                                                    shape         = RoundedCornerShape(14.dp),
                                                    colors        = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor      = AccentPurple,
                                                        unfocusedBorderColor    = AccentPurple.copy(alpha = 0.3f),
                                                        focusedContainerColor   = AccentPurple.copy(alpha = 0.06f),
                                                        unfocusedContainerColor = AccentPurple.copy(alpha = 0.06f),
                                                        focusedTextColor        = TextPrimary,
                                                        unfocusedTextColor      = TextPrimary,
                                                        cursorColor             = AccentPurple
                                                    ),
                                                    leadingIcon = {
                                                        Icon(fieldIcon, null, tint = AccentPurple, modifier = Modifier.size(18.dp))
                                                    },
                                                    singleLine = true,
                                                    textStyle  = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                                )
                                            }

                                            // ── Error ────────────────────────
                                            AnimatedVisibility(visible = uiState.saveError != null) {
                                                Text(
                                                    uiState.saveError ?: "",
                                                    color    = ErrorRed,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp)
                                                )
                                            }

                                            HorizontalDivider(color = Color.Black.copy(alpha = 0.07f))

                                            // ── Guardar ──────────────────────
                                            TextButton(
                                                onClick = {
                                                    viewModel.updateUser(
                                                        name       = if (editingField == "name")       fieldValue else nameField,
                                                        email      = if (editingField == "email")      fieldValue else emailField,
                                                        nationalId = if (editingField == "nationalId") fieldValue else nationalIdField
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                                enabled  = !uiState.isSaving
                                            ) {
                                                if (uiState.isSaving) {
                                                    CircularProgressIndicator(color = AccentPurple, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                                } else {
                                                    Text("Guardar", color = AccentPurple, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                }
                                            }

                                            HorizontalDivider(color = Color.Black.copy(alpha = 0.07f))

                                            // ── Cancelar ─────────────────────
                                            TextButton(
                                                onClick  = { editingField = null; viewModel.clearSaveError() },
                                                modifier = Modifier.fillMaxWidth().height(56.dp)
                                            ) {
                                                Text("Cancelar", color = TextSecondary, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Componentes ──────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Brush.linearGradient(listOf(AccentPurpleLight, AccentPurple)))
        )
        Spacer(Modifier.width(8.dp))
        Text(title, color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.8.sp)
    }
}

@Composable
fun ProfileRowCard(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(AccentPurple.copy(alpha = 0.55f), AccentPurple.copy(alpha = 0.25f)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(3.dp))
                Text(value.ifBlank { "—" }, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(AccentPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = AccentPurple, modifier = Modifier.size(18.dp))
            }
        }
    }
}