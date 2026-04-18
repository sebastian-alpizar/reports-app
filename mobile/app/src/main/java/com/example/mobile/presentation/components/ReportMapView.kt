package com.example.mobile.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mobile.domain.model.Location
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView as MapLibreMapView

@Composable
fun ReportMapView(
    currentLocation: Location?,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {}
) {
    val context = LocalContext.current
    val mapLibreMapView = remember { MapLibreMapView(context) }
    val mapState = remember { mutableStateOf<MapLibreMap?>(null) }
    val styleLoaded = remember { mutableStateOf(false) }

    LaunchedEffect(currentLocation, styleLoaded.value) {
        android.util.Log.d(
            "MAP_DEBUG",
            "Location=$currentLocation, styleLoaded=${styleLoaded.value}"
        )
        val map = mapState.value
        if (map != null && styleLoaded.value && currentLocation != null) {
            val targetLatLng = LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            )
            map.animateCamera(
                org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(
                    org.maplibre.android.camera.CameraPosition.Builder()
                        .target(targetLatLng)
                        .zoom(15.0)
                        .build()
                ),
                1500
            )
            val markerPosition = LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            )

            map.addMarker(
                org.maplibre.android.annotations.MarkerOptions()
                    .position(markerPosition)
                    .title("Mi ubicación")
            )
            android.util.Log.d(
                "MAP_DEBUG",
                "Moviendo cámara a ${currentLocation.latitude}, ${currentLocation.longitude}"
            )
        }
    }

    AndroidView(
        factory = { _ ->
            mapLibreMapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(maplibreMap: MapLibreMap) {
                        mapState.value = maplibreMap
                        maplibreMap.setStyle(
                            "https://tiles.openfreemap.org/styles/liberty"
                        ) {
                            styleLoaded.value = true
                            android.util.Log.d("MAP_DEBUG", "Style cargado")
                            onMapReady()
                        }
                    }
                })
            }
        },
        modifier = modifier
    )
}