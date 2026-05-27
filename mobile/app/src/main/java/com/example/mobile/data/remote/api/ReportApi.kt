package com.example.mobile.data.remote.api


import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.data.remote.dto.UpdateStatusRequest
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

    @Multipart
    @PUT("reports/{id}")
    suspend fun updateReport(
        @Path("id") id: Long,
        @Part("report") report: RequestBody,
        @Part photo: MultipartBody.Part?
    )

    @GET("reports")
    suspend fun getAllReports(): List<ReportResponse>

    @GET("reports/user/{userId}")
    suspend fun getReportsByUser(
        @Path("userId") userId: Long?
    ): List<ReportResponse>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @PATCH("reports/{id}/status")
    suspend fun updateReportStatus(
        @Path("id") id: Long,
        @Body body: UpdateStatusRequest
    )

    @DELETE("reports/{id}")
    suspend fun deleteReport(
        @Path("id") id: Long
    )


    @GET("reports/nearby")
    suspend fun getNearbyReports(
        @Query("lat") latitude: Double?,
        @Query("lng") longitude: Double?,
        @Query("radiusKm") radiusKm: Double = 5.0
    ): List<ReportResponse>
}
