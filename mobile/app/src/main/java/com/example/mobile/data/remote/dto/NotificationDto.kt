package com.example.mobile.data.remote.dto

data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val reportId: Long,
    val read: Boolean,
    val createdAt: String
)