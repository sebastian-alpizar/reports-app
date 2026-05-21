package com.example.mobile.domain.model


data class Report(
    val id: String = "",
    val location: Location,
    val description: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)