package com.example.mobile.data.repository

import android.content.Context
import android.net.Uri
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.ReportRequest
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.data.remote.dto.UpdateStatusRequest
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.model.ReportStatus
import com.example.mobile.domain.repository.ReportRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    override suspend fun sendReport(context: Context, report: Report): Result<Unit> {
        return try {
            val reportRequest = ReportRequest(
                description         = report.description,
                latitude            = report.location.latitude,
                longitude           = report.location.longitude,
                approximateLocation = null,
                category            = null
            )
            val reportJson = Gson().toJson(reportRequest)

            val reportBody = reportJson.toRequestBody("application/json".toMediaTypeOrNull())

            val imageUri   = Uri.parse(report.imageUri)

            val inputStream = context.contentResolver.openInputStream(imageUri)

            val file = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }


            val photoRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

            val photoPart = MultipartBody.Part.createFormData("photo", file.name, photoRequestBody)
            reportApi.sendReport(report = reportBody, photo = photoPart)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReport(
        context: Context,
        reportId: String,
        description: String,
        imageUri: String?
    ): Result<Unit> {
        return try {
            val reportJson = Gson().toJson(mapOf("description" to description))
            val reportBody = reportJson.toRequestBody("application/json".toMediaTypeOrNull())

            val photoPart: MultipartBody.Part? = imageUri?.let { uri ->
                val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                context.contentResolver.openInputStream(Uri.parse(uri))
                    ?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                file.asRequestBody("image/*".toMediaTypeOrNull())
                    .let { MultipartBody.Part.createFormData("photo", file.name, it) }
            }

            reportApi.updateReport(reportId.toLong(), reportBody, photoPart)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllReports(): Result<List<ReportResponse>> {
        return try {
            Result.success(reportApi.getAllReports())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReportStatus(id: Long, status: ReportStatus): Result<Unit> {
        return try {
            reportApi.updateReportStatus(id, UpdateStatusRequest(status = status.name))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getNearbyReports(
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double
    ): Result<List<Report>> {
        return try {
            val reports = reportApi.getNearbyReports(latitude, longitude, radiusKm)
            Result.success(reports.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    private fun ReportResponse.toDomain() = Report(
        id                  = this.id.toString(),
        location            = Location(latitude = this.latitude, longitude = this.longitude),
        description         = this.description,
        approximateLocation = this.approximateLocation,
        category            = this.category,
        status              = this.status,
        photoUrl            = this.photoUrl,
        userId              = this.userId,
        userName            = this.userName
    )
}
