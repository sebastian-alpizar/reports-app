package com.example.mobile.domain.usecase.location

import com.example.mobile.domain.repository.LocationRepository
import javax.inject.Inject

class HasLocationPermissionUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Boolean {
        return repository.hasLocationPermission()
    }
}