package com.example.mobile.presentation.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
//import org.maplibre.android.maps.MapView as MapLibreMapView


private const val ICON_REPORT_ID = "report_id"

@Composable
fun ReportMapView(
    currentLocation: Location?,
    nearbyReports: List<Report>,
    shouldCenterMap: Boolean = false,
    onMapCentered: () -> Unit = {},
    onReportClicked: (Report) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Usamos MapView directamente (el mismo que usaba el mapa original)
    val mapView         = remember { MapView(context) }
    val mapState        = remember { mutableStateOf<MapLibreMap?>(null) }
    val styleLoaded     = remember { mutableStateOf(false) }
    val initialCentered = remember { mutableStateOf(false) }
    val reportMarkers   = remember { mutableStateOf<List<Marker>>(emptyList()) }
    val reportsRef      = remember { mutableStateOf<List<Report>>(emptyList()) }
    reportsRef.value = nearbyReports

    // Mover cámara + dibujar marcadores cuando cambia ubicación o estilo carga
    LaunchedEffect(currentLocation, shouldCenterMap, styleLoaded.value) {
        val map = mapState.value ?: return@LaunchedEffect
        if (!styleLoaded.value || currentLocation == null) return@LaunchedEffect

        val target    = LatLng(currentLocation.latitude, currentLocation.longitude)
        val shouldMove = !initialCentered.value || shouldCenterMap

        if (shouldMove) {
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(target).zoom(15.0).build()
                ),
                1500
            )
            initialCentered.value = true
            onMapCentered()
        }

        // Limpiar todo y redibujar
        reportMarkers.value.forEach { map.removeMarker(it) }
        map.clear()

        // Marcador del usuario (pin por defecto de MapLibre)
        map.addMarker(MarkerOptions().position(target).title("Mi ubicación"))

        // Marcadores de reportes
        val iconFactory  = IconFactory.getInstance(context)
        val icon         = iconFactory.fromBitmap(createReportIcon())
        val newMarkers   = nearbyReports.map { report ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(report.location.latitude, report.location.longitude))
                    .icon(icon)
                    .title(report.description.take(60))
            )
        }
        reportMarkers.value = newMarkers
    }

    // Redibujar solo marcadores de reportes cuando cambia la lista
    LaunchedEffect(nearbyReports) {
        val map = mapState.value ?: return@LaunchedEffect
        if (!styleLoaded.value) return@LaunchedEffect

        reportMarkers.value.forEach { map.removeMarker(it) }
        val iconFactory = IconFactory.getInstance(context)
        val icon        = iconFactory.fromBitmap(createReportIcon())
        val newMarkers  = nearbyReports.map { report ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(report.location.latitude, report.location.longitude))
                    .icon(icon)
                    .title(report.description.take(60))
            )
        }
        reportMarkers.value = newMarkers
    }

    DisposableEffect(Unit) {
        mapView.onStart()
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(maplibreMap: MapLibreMap) {
                        mapState.value = maplibreMap

                        // Mismo estilo del mapa original
                        maplibreMap.setStyle("https://tiles.openfreemap.org/styles/liberty") {
                            styleLoaded.value = true
                        }

                        // Click en marcador → busca el reporte y muestra detalle
                        maplibreMap.setOnMarkerClickListener { marker ->
                            val report = reportsRef.value.find {
                                it.location.latitude == marker.position.latitude &&
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
    val size   = 44
    val bmp    = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val paint  = Paint(Paint.ANTI_ALIAS_FLAG)
    // Fondo amarillo redondeado
    paint.color = Color(0xFFF59E0B).toArgb()
    canvas.drawRoundRect(RectF(4f, 4f, size - 4f, size - 4f), 12f, 12f, paint)
    // Símbolo "!" blanco
    paint.color     = Color.White.toArgb()
    paint.strokeWidth = 5f
    paint.strokeCap   = Paint.Cap.ROUND
    canvas.drawLine(size / 2f, 12f, size / 2f, 26f, paint)
    canvas.drawCircle(size / 2f, 33f, 3f, paint)
    return bmp
}