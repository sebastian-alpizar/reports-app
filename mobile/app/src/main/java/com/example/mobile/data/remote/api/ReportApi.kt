package com.example.mobile.data.remote.api


import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.data.remote.dto.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ReportApi {

    @Multipart
    @POST("reports")
    suspend fun sendReport(
        @Part("report") report: RequestBody,
        @Part photo: MultipartBody.Part
    )

    @GET("reports")
    suspend fun getAllReports(): List<ReportResponse>

    @PATCH("reports/{id}/status")
    suspend fun updateReportStatus(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    )

    @GET("reports/user/{userId}")
    suspend fun getReportsByUser(
        @Path("userId") userId: Long?
    ): List<ReportResponse>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto
}