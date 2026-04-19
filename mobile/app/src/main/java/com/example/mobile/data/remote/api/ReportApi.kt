package com.example.mobile.data.remote.api

import com.example.mobile.data.remote.dto.ReportRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReportApi {
    @Multipart
    @POST("reports")
    suspend fun sendReport(
        @Part("report") report: RequestBody,  // JSON como string
        @Part photo: MultipartBody.Part       // Archivo de foto
    )
}