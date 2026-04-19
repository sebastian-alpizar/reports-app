package com.example.mobile.domain.repository

import com.example.mobile.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?
    fun getLocationUpdates(): Flow<Location>
    fun hasLocationPermission(): Boolean
}