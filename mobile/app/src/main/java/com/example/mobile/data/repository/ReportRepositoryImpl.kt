package com.example.mobile.data.repository

import android.content.Context
import android.net.Uri
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.dto.CreateReportDto
import com.example.mobile.data.remote.dto.ReportRequest
import com.example.mobile.data.remote.dto.ReportResponse
import com.example.mobile.data.remote.dto.StatisticsResponse
import com.example.mobile.data.remote.dto.UpdateStatusRequest
import com.example.mobile.data.remote.util.ErrorParser
import com.example.mobile.domain.model.Report
import com.example.mobile.domain.model.ReportStatus
import com.example.mobile.domain.repository.ReportRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject
import com.example.mobile.domain.model.Location

class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {

    override suspend fun sendReport(context: Context, report: CreateReportDto): Result<Unit> {
        return try {
            val reportRequest = ReportRequest(
                description         = report.description,
                latitude            = report.location.latitude,
                longitude           = report.location.longitude,
                approximateLocation = report.approximateLocation,
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

        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateReport(
        context: Context,
        reportId: Long,
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
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getAllReports(): Result<List<Report>> {
        return try {
            val result = reportApi.getAllReports()
                .map { it.toDomain() }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReportsByUser(userId: Long): Result<List<Report>> {
        return try {
            val result = reportApi.getReportsByUser(userId)
                .map { it.toDomain() }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    override suspend fun updateReportStatus(id: Long, status: ReportStatus): Result<Unit> {
//        return try {
//            reportApi.updateReportStatus(id, UpdateStatusRequest(status = status.name))
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }


    override suspend fun getNearbyReports(
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double
    ): Result<List<Report>> {
        return try {
            val reports = reportApi.getNearbyReports(latitude, longitude, radiusKm)
                .map {it.toDomain()}
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    override suspend fun deleteReport(reportId: Long): Result<Unit> {
        return try {
            reportApi.deleteReport(reportId)
            Result.success(Unit)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun voteReport(
        reportId: Long
    ): Result<String> {
        return try {
            val result = reportApi.voteReport(reportId)
            Result.success(result.message)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateReportStatus(id: Long, status: UpdateStatusRequest): Result<String> {
        return try {
            val result = reportApi.updateReportStatus(id, status)
            Result.success(result.message)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    override suspend fun getStatistics(): Result<StatisticsResponse> {
        return try {
            val result = reportApi.getStatistics()
            Result.success(result.data!!)
        } catch (e: HttpException) {
            val message = ErrorParser.parseError(e)
            Result.failure(Exception(message))
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun ReportResponse.toDomain() = Report(
        id = id,
        location = Location(
            latitude = latitude,
            longitude = longitude
        ),
        description = description,
        status = status,
        approximateLocation = approximateLocation,
        category = category,
        photoUrl = photoUrl,
        userId = userId,
        userName = userName,
        userEmail = userEmail,
        reportDate = reportDate,
//        latitude = latitude,
//        longitude = longitude,
        severity = severity,
        affectedUsers = affectedUsers,
        priorityLevel = priorityLevel,
        userHasVoted = userHasVoted
    )

//    private fun ReportResponse.toDomain() = Report(
//        id                  = this.id.toString(),
//        location            = Location(latitude = this.latitude, longitude = this.longitude),
//        description         = this.description,
//        approximateLocation = this.approximateLocation,
//        category            = this.category,
//        status              = this.status,
//        photoUrl            = this.photoUrl,
//        userId              = this.userId,
//        userName            = this.userName,
//    )
}

