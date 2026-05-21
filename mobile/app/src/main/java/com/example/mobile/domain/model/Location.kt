package com.example.mobile.domain.model

data class Location(
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
)