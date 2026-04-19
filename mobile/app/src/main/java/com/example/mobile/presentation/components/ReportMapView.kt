package com.example.mobile.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mobile.domain.model.Location
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.MapView as MapLibreMapView

@Composable
fun ReportMapView(
    currentLocation: Location?,
    shouldCenterMap: Boolean,
    onMapCentered: () -> Unit,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {}
) {
    val context = LocalContext.current
    val mapLibreMapView = remember { MapLibreMapView(context) }
    val mapState = remember { mutableStateOf<MapLibreMap?>(null) }
    val styleLoaded = remember { mutableStateOf(false) }

    // Para centrar solo una vez al inicio
    val initialCentered = remember { mutableStateOf(false) }

    LaunchedEffect(
        currentLocation,
        shouldCenterMap,
        styleLoaded.value
    ) {
        val map = mapState.value

        if (
            map != null &&
            styleLoaded.value &&
            currentLocation != null
        ) {
            val targetLatLng = LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            )

            val shouldMoveCamera =
                !initialCentered.value || shouldCenterMap

            if (shouldMoveCamera) {
                map.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(targetLatLng)
                            .zoom(15.0)
                            .build()
                    ),
                    1500
                )

                // Evita recentrado infinito
                initialCentered.value = true
                onMapCentered()
            }
            // limpiar markers anteriores
            map.clear()
            // agregar marker actual
            map.addMarker(
                MarkerOptions()
                    .position(targetLatLng)
                    .title("Mi ubicación")
            )
        }
    }

    AndroidView(
        factory = {
            mapLibreMapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(maplibreMap: MapLibreMap) {
                        mapState.value = maplibreMap
                        maplibreMap.setStyle(
                            "https://tiles.openfreemap.org/styles/liberty"
                        ) {
                            styleLoaded.value = true
                            onMapReady()
                        }
                    }
                })
            }
        },
        modifier = modifier
    )
}