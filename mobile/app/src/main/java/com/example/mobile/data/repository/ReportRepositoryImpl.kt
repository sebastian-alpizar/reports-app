package com.example.mobile.data.repository

import android.content.Context
import android.net.Uri
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.ReportRequest
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.repository.ReportRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    override suspend fun sendReport(
        context: Context,
        report: Report
    ): Result<Unit> {
        return try {

            val reportRequest = ReportRequest(
                description = report.description,
                latitude = report.location.latitude,
                longitude = report.location.longitude,
                approximateLocation = null,
                category = null
            )

            val reportJson = Gson().toJson(reportRequest)

            val reportBody = reportJson.toRequestBody(
                "application/json".toMediaTypeOrNull()
            )

            val imageUri = Uri.parse(report.imageUri)

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File.createTempFile(
                "upload",
                ".jpg",
                context.cacheDir
            )

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val photoRequestBody = file.asRequestBody(
                "image/*".toMediaTypeOrNull()
            )

            val photoPart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                photoRequestBody
            )

            reportApi.sendReport(
                report = reportBody,
                photo = photoPart
            )

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}