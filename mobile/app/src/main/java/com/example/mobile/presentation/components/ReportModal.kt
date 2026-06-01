@file:OptIn(ExperimentalPermissionsApi::class)
package com.example.mobile.presentation.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.mobile.domain.model.Location
import com.example.mobile.presentation.components.snackbar.AppSnackbar
import com.example.mobile.presentation.components.snackbar.SnackbarState
import com.example.mobile.presentation.home.ReportFormState
import com.example.mobile.presentation.utils.GlassModifiers
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportModal(
    currentLocation: Location?,
    currentAddress: String?,
    isSubmitting: Boolean,
    snackbarState: SnackbarState,
    reportFormState: ReportFormState,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (String?) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Permission states
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryPermissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    // Temporary URI for camera capture
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { uri ->
                onImageSelected(uri.toString())
            }
        }
        cameraImageUri = null
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AppSnackbar(
                message = snackbarState.message,
                isError = snackbarState.isError,
                visible = snackbarState.isVisible,
                onDismiss = {
                    scope.launch {
                        snackbarState.dismiss()
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .then(GlassModifiers.glassCard())
                    .heightIn(max = 600.dp) // Altura máxima
            ) {

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Contenido principal con scroll
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp)
                        .padding(top = 8.dp)
                        .verticalScroll(rememberScrollState()), // Scroll añadido
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Text(
                        text = "Reportar Incidente",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Muestra dirección si está disponible, coordenadas si no
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.20f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color(0xFF7C3AED)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ubicación actual",
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = when {
                                        currentAddress != null -> currentAddress
                                        currentLocation != null -> "${currentLocation.latitude}, ${currentLocation.longitude}"
                                        else -> "Obteniendo ubicación..."
                                    },
                                    fontSize = 13.sp,
                                    color = Color(0xFF7C3AED)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de descripción
                    AppTextField(
                        value = reportFormState.description,
                        onValueChange = { onDescriptionChange(it) },
                        label = "Descripción",
                        modifier = Modifier.fillMaxWidth(),
                        isError = reportFormState.descriptionError != null
                    )

                    reportFormState.descriptionError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones de imagen
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botón Cámara
                            OutlinedButton(
                                onClick = {
                                    // Limpiar la imagen seleccionada actual antes de tomar nueva
                                    onImageSelected(null)
                                    handleCameraClick(
                                        context = context,
                                        permissionState = cameraPermissionState,
                                        launcher = cameraLauncher,
                                        onTempUriCreated = {
                                            cameraImageUri = it
                                        }
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.20f)
                                ),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Cámara",
                                    tint = Color(0xFF7C3AED)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Cámara",
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Botón Galería
                            OutlinedButton(
                                onClick = {
                                    onImageSelected(null)
                                    handleGalleryClick(
                                        permissionState = galleryPermissionState,
                                        launcher = galleryLauncher
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White.copy(alpha = 0.20f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Image,
                                    contentDescription = "Galería",
                                    tint = Color(0xFF7C3AED)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Galería",
                                    color = Color.White.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        reportFormState.imageError?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            )
                        }
                    }

                    // Vista previa de la imagen seleccionada
                    reportFormState.selectedImageUri?.let { imageUri ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF2C2B2E))
                                .clickable {
                                    onImageSelected(null)
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.6f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Enviar
                    AppButton(
                        text = "Enviar Reporte",
                        isLoading = isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(bottom = 8.dp), // Padding inferior extra
                        enabled = currentLocation != null && !isSubmitting
                    ) {
                        onSubmit()
                    }
                }
            }
        }
    }
}

private fun handleGalleryClick(
    permissionState: PermissionState,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    if (permissionState.status.isGranted) {
        launcher.launch("image/*")
    } else {
        permissionState.launchPermissionRequest()
    }
}

private fun handleCameraClick(
    context: Context,
    permissionState: PermissionState,
    launcher: ManagedActivityResultLauncher<Uri, Boolean>,
    onTempUriCreated: (Uri) -> Unit
) {
    if (permissionState.status.isGranted) {
        // Generar nombre único con timestamp
        val timestamp = System.currentTimeMillis()
        val file = File(
            context.cacheDir,
            "temp_camera_image_$timestamp.jpg"
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        onTempUriCreated(uri)
        launcher.launch(uri)

        // Limpiar archivos temporales antiguos (mayores a 24 horas)
        cleanOldTempImages(context)
    } else {
        permissionState.launchPermissionRequest()
    }
}

// Función para limpiar archivos temporales viejos
private fun cleanOldTempImages(context: Context, maxAge: Long = 24 * 60 * 60 * 1000) {
    val cacheDir = context.cacheDir
    val now = System.currentTimeMillis()
    cacheDir.listFiles()?.forEach { file ->
        if (file.name.startsWith("temp_camera_image_") && now - file.lastModified() > maxAge) {
            file.delete()
        }
    }
}