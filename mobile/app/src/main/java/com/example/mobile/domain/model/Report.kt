package com.example.mobile.domain.model

data class Report(
    val id: String = "",
    val location: Location,
    val description: String,
    val imageUri: String? = null,
    val approximateLocation: String? = null,
    val category: String? = null,
    val status: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)