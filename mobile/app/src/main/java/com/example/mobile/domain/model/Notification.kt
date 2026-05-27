package com.example.mobile.domain.model

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val reportId: Long,
    val read: Boolean,
    val createdAt: String
)