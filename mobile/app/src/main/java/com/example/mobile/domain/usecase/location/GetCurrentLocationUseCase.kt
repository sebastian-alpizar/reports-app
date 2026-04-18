package com.example.mobile.domain.usecase.location

import com.example.mobile.domain.model.Location
import com.example.mobile.domain.repository.LocationRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(): Location? {
        return repository.getCurrentLocation()
    }
}