package com.example.mobile.presentation.admin

import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.model.ReportStatus

data class AdminUiState(
    val reports: List<ReportResponse> = emptyList(),
    val filteredReports: List<ReportResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedStatus: ReportStatus? = null,
    val selectedCategory: String? = null

)