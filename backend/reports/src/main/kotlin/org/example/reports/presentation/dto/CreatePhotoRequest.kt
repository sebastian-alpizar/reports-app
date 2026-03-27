package org.example.reports.presentation.dto

data class CreatePhotoRequest(
    val url: String,
    val reportId: Long
)