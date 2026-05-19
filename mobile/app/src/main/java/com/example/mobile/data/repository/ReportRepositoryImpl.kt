package com.example.mobile.data.repository

import android.content.Context
import android.net.Uri
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.ReportRequest
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.domain.model.Location
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.repository.ReportRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.example.reports.domain.model.ReportStatus
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    override suspend fun sendReport(
        context: Context,
        report: Report
    ): Result<Report> {
        return try {
            val reportRequest = ReportRequest(
                description = report.description,
                latitude = report.location.latitude,
                longitude = report.location.longitude,
                approximateLocation = report.approximateLocation,
                category = report.category)

            val reportJson = Gson().toJson(reportRequest)
            val reportBody = reportJson.toRequestBody("application/json".toMediaTypeOrNull())

            val imageUri = Uri.parse(report.imageUri)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }

            val photoRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, photoRequestBody)

            val response = reportApi.sendReport(report = reportBody, photo = photoPart)
            Result.success(response.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyReports(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<Report>> {
        return try {
            val reports = reportApi.getNearbyReports(latitude, longitude, radiusKm)
            Result.success(reports.map { it.toDomain() })
        } catch (e: HttpException) {
            Result.failure(Exception("Error al obtener reportes: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    override suspend fun getMyReports(): Result<List<Report>> {
        return try {
            val reports = reportApi.getMyReports()
            Result.success(reports.map { it.toDomain() })
        } catch (e: HttpException) {
            Result.failure(Exception("Error al obtener tus reportes: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    override suspend fun getAllReports(): Result<List<ReportResponse>> {
        return try {
            val reports = reportApi.getAllReports()
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReportStatus(
        id: Long,
        status: ReportStatus
    ): Result<Unit> {
        return try {
            reportApi.updateReportStatus(id, mapOf("status" to status.name))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Mapper ---
    private fun ReportResponse.toDomain(): Report {
        return Report(
            id = this.id.toString(),
            location = Location(
                latitude = this.latitude,
                longitude = this.longitude
            ),
            description = this.description,
            approximateLocation = this.approximateLocation,
            category = this.category,
            status = this.status
        )
    }
}