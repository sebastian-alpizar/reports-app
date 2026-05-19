package com.example.mobile.data.remote.api

import com.example.mobile.data.remote.dto.ReportRequest
import com.example.mobile.data.remote.dto.ReportResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApi {
    @Multipart
    @POST("reports")
    suspend fun sendReport(
        @Part("report") report: RequestBody,  // JSON como string
        @Part photo: MultipartBody.Part       // Archivo de foto
    ) : ReportResponse

    /**
     * Obtiene reportes cercanos a una coordenada.
     * El backend filtra por distancia en kilómetros.
     */

    @GET("reports/nearby")
    suspend fun getNearbyReports(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radiusKm") radiusKm: Double = 5.0
    ): List<ReportResponse>

    @GET("reports/my")
    suspend fun getMyReports(): List<ReportResponse>

    @GET("reports")
    suspend fun getAllReports(): List<ReportResponse>

    @PATCH("reports/{id}/status")
    suspend fun updateReportStatus(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    )
}