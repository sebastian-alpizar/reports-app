package com.example.mobile.domain.model


data class Report(
    val id: String = "",
    val location: Location,
    val description: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String? = null,
    val approximateLocation: String? = null,
    val category: String? = null,
    val photoUrl: String?= null,
    val userId: Long? = null,
    val userName: String? = null
)