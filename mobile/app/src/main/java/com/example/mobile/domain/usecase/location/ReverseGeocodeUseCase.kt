package com.example.mobile.domain.usecase.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class ReverseGeocodeUseCase @Inject constructor(){

    suspend operator fun invoke(
        context: Context,
        latitude: Double?,
        longitude: Double?
    ): String? = withContext(Dispatchers.IO) {

        val lat = latitude  ?: return@withContext null
        val lng = longitude ?: return@withContext null

        return@withContext try {
            val geocoder = Geocoder(context, Locale.forLanguageTag("es"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        cont.resume(formatAddress(addresses.firstOrNull()))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                formatAddress(addresses?.firstOrNull())
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun formatAddress(address: android.location.Address?): String? {
        if (address == null) return null

        val parts = mutableListOf<String>()

        // featureName si es un lugar reconocido
        val feature = address.featureName
        if (feature != null && !feature.matches(Regex("^[0-9A-Z]{4}\\+.*")) && !feature.all { it.isDigit() }) {
            parts.add(feature)
        }

        // thoroughfare la calle si no hay featureName útil
        if (parts.isEmpty()) {
            address.thoroughfare?.let { parts.add(it) }
        }

        // subLocality → locality → subAdminArea (va del más específico al menos)
        address.subLocality?.let { parts.add(it) }
            ?: address.locality?.let { parts.add(it) }
            ?: address.subAdminArea?.let { parts.add(it) }

        // provincia siempre al final 
        address.adminArea?.let { admin ->
            if (parts.none { it.equals(admin, ignoreCase = true) }) {
                parts.add(admin)
            }
        }

        return if (parts.isNotEmpty()) parts.joinToString(", ") else null
    }
}
