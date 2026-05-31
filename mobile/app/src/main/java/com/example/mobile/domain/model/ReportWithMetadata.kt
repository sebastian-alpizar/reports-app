package com.example.mobile.domain.model

data class ReportWithMetada(
    val id: Long,
    val location: Location,
    val description: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String? = null,
    val approximateLocation: String? = null,
    val category: String? = null,
    val photoUrl: String?= null,
    val userId: Long? = null,
    val userName: String? = null,

    val severity: Int = 1,
    val affectedUsers: Int = 0,
    val priorityLevel: PriorityLevel,
    //val userHasVoted: Boolean = false
)