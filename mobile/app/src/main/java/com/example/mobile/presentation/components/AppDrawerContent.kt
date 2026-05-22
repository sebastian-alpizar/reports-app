package com.example.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private val AccentPurple = Color(0xFF7C3AED)
private val AccentPurpleLight = Color(0xFF9F67FA)
private val DangerRed = Color(0xFFB71C1C)

@Composable
fun AppDrawerContent(
    userName: String,
    userEmail: String,
    onHistorial: () -> Unit,
    onPerfil: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(290.dp),
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.8f))
        ) {

            Spacer(
                Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(0.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AccentPurpleLight, AccentPurple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        userName,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        userEmail,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            DrawerItem(
                icon = Icons.Default.History,
                label = "Historial",
                onClick = onHistorial
            )

            DrawerItem(
                icon = Icons.Default.Person,
                label = "Mi Perfil",
                onClick = onPerfil
            )

            Spacer(Modifier.weight(1f))

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color.White
            )

            Spacer(Modifier.height(4.dp))

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Cerrar sesión",
                labelColor = DangerRed,
                backgroundBrush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFD32F2F),
                        Color(0xFFEF5350)
                    )
                ),
                onClick = onLogout
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: Color = Color.White,
    labelColor: Color = Color.Black,
    backgroundBrush: Brush = Brush.horizontalGradient(
        listOf(
            Color(0xFF7C3AED),
            Color(0xFFA78BFA)
        )
    )
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Text(
            label,
            color = labelColor,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}