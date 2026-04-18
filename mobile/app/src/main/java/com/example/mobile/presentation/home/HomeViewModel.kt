package com.example.mobile.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.usecase.location.GetLocationUpdatesUseCase
import com.example.mobile.domain.usecase.location.HasLocationPermissionUseCase
import com.example.mobile.presentation.utils.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val hasLocationPermissionUseCase: HasLocationPermissionUseCase
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    private val _shouldRequestPermission = MutableStateFlow(false)
    val shouldRequestPermission = _shouldRequestPermission.asStateFlow()

    init {
        checkPermissionAndStartUpdates()
    }

    private fun checkPermissionAndStartUpdates() {
        viewModelScope.launch {
            if (hasLocationPermissionUseCase()) {
                startLocationUpdates()
            } else {
                _shouldRequestPermission.value = true
                _isLoading.value = false
            }
        }
    }

    private fun startLocationUpdates() {
        viewModelScope.launch {
            getLocationUpdatesUseCase()
                .catch { exception ->
                    _event.emit(UiEvent.ShowSnackbar(
                        "Error al obtener ubicación: ${exception.message}",
                        true
                    ))
                    _isLoading.value = false
                }
                .collect { location ->
                    _currentLocation.value = location
                    if (_isLoading.value) {
                        _isLoading.value = false
                    }
                }
        }
    }

    fun onPermissionGranted() {
        _shouldRequestPermission.value = false
        startLocationUpdates()
    }

    fun onPermissionDenied() {
        _shouldRequestPermission.value = false
        viewModelScope.launch {
            _event.emit(UiEvent.ShowSnackbar(
                "Permiso de ubicación necesario para mostrar el mapa",
                true
            ))
        }
    }
}