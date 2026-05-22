package com.example.mobile.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.MapView as MapLibreMapView
import android.util.Log
@Composable
fun ReportMapView(
    currentLocation: Location?,
    shouldCenterMap: Boolean,
    onMapCentered: () -> Unit,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {},
    nearbyReports: List<Report> = emptyList(),
    onReportClicked: (Report) -> Unit = {}
) {
    val context = LocalContext.current
    val mapLibreMapView = remember { MapLibreMapView(context) }
    val mapState = remember { mutableStateOf<MapLibreMap?>(null) }
    val styleLoaded = remember { mutableStateOf(false) }

    // Para centrar solo una vez al inicio
    val initialCentered = remember { mutableStateOf(false) }

    val reportMarkers   = remember { mutableStateOf<List<Marker>>(emptyList()) }
    val reportsRef      = remember { mutableStateOf<List<Report>>(emptyList()) }
    reportsRef.value = nearbyReports

    LaunchedEffect(
        currentLocation,
        shouldCenterMap,
        styleLoaded.value) {

        val map = mapState.value ?: return@LaunchedEffect
        if (!styleLoaded.value || currentLocation == null) return@LaunchedEffect

        val targetLatLng    = LatLng(currentLocation.latitude!!, currentLocation.longitude!!)
        val shouldMoveCamera = !initialCentered.value || shouldCenterMap

        if (shouldMoveCamera) {
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(targetLatLng).zoom(15.0).build()
                ),
                1500
            )
            initialCentered.value = true
            onMapCentered()
        }

        // Limpiar marcadores anteriores (usuario + reportes)
        reportMarkers.value.forEach { map.removeMarker(it) }
        map.clear()

        // Marcador del usuario
        map.addMarker(MarkerOptions().position(targetLatLng).title("Mi ubicación"))

        // Marcadores de reportes cercanos
        val iconFactory = IconFactory.getInstance(context)
        val icon        = iconFactory.fromBitmap(createReportIcon())
        reportMarkers.value = nearbyReports.map { report ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(report.location.latitude!!, report.location.longitude!!))
                    .icon(icon)
                    .title(report.description.take(60))
            )
        }
    }


    LaunchedEffect(nearbyReports) {

        val map = mapState.value ?: return@LaunchedEffect
        if (!styleLoaded.value) return@LaunchedEffect
        reportMarkers.value.forEach { map.removeMarker(it) }
        if (nearbyReports.isEmpty()) return@LaunchedEffect
        val iconFactory = IconFactory.getInstance(context)
        val icon = iconFactory.fromBitmap(createReportIcon())
        reportMarkers.value = nearbyReports.map { report ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(report.location.latitude!!, report.location.longitude!!))
                    .icon(icon)
                    .title(report.description.take(60))
            )
        }
        Log.d("MAP_REPORTS", "Cantidad: ${nearbyReports.size}")

        nearbyReports.forEach {
            Log.d(
                "MAP_REPORTS",
                "${it.description} -> ${it.location.latitude}, ${it.location.longitude}"
            )
        }
    }

    AndroidView(
        factory = {
            mapLibreMapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(maplibreMap: MapLibreMap) {
                        mapState.value = maplibreMap
                        maplibreMap.setStyle("https://tiles.openfreemap.org/styles/liberty") {
                            styleLoaded.value = true
                            onMapReady()
                        }
                        // Nuevo — click en marcador de reporte
                        maplibreMap.setOnMarkerClickListener { marker ->
                            val report = reportsRef.value.find {
                                it.location.latitude  == marker.position.latitude &&
                                        it.location.longitude == marker.position.longitude
                            }
                            report?.let { onReportClicked(it) }
                            true
                        }
                    }
                })
            }
        },
        modifier = modifier
    )
}

private fun createReportIcon(): Bitmap {
    val size = 64

    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Fondo
    paint.color = Color(0xFFF59E0B).toArgb()

    canvas.drawRoundRect(
        RectF(
            size * 0.08f,
            size * 0.08f,
            size * 0.92f,
            size * 0.92f
        ),
        size * 0.22f,
        size * 0.22f,
        paint
    )

    // Símbolo blanco
    paint.color = Color.White.toArgb()
    paint.strokeWidth = size * 0.09f
    paint.strokeCap = Paint.Cap.ROUND

    // Línea vertical
    canvas.drawLine(
        size / 2f,
        size * 0.27f,
        size / 2f,
        size * 0.58f,
        paint
    )

    // Punto
    canvas.drawCircle(
        size / 2f,
        size * 0.74f,
        size * 0.05f,
        paint
    )

    return bmp
}