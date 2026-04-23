package com.example.mobile.data.repository

import com.example.mobile.data.remote.api.AuthApi
import com.example.mobile.data.remote.dto.LoginRequest
import com.example.mobile.data.remote.dto.RegisterRequest
import com.example.mobile.domain.repository.AuthRepository
import retrofit2.HttpException
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            val response = api.login(
                LoginRequest(email, password)
            )

            Result.success(response.data.token)

        } catch (e: HttpException) {

            val message = parseError(e)

            Result.failure(Exception(message))

        } catch (e: Exception) {

            Result.failure(Exception("Error de conexión"))

        }
    }

    override suspend fun register(
        name: String,
        email: String,
        nationalId: String,
        password: String
    ): Result<String> {

        return try {
            val response = api.register(
                RegisterRequest(name, email, nationalId, password)
            )
            Result.success(response.data.token)

        } catch (e: HttpException) {
            val message = parseError(e)
            Result.failure(Exception(message))

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }

    private fun parseError(e: HttpException): String {

        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val json = JSONObject(errorBody ?: "")
            json.getString("message")

        } catch (ex: Exception) {
            "Error del servidor"
        }
    }
}