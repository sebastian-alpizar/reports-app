package com.example.mobile.domain.usecase.location

import com.example.mobile.domain.model.Location
import com.example.mobile.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUpdatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<Location> {
        return repository.getLocationUpdates()
    }
}