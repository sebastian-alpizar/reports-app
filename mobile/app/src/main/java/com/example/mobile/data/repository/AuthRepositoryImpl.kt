package com.example.mobile.data.repository

import android.util.Base64
import com.example.mobile.core.util.TokenManager
import com.example.mobile.data.remote.api.AuthApi
import com.example.mobile.data.remote.dto.LoginRequest
import com.example.mobile.data.remote.dto.RegisterRequest
import com.example.mobile.domain.repository.AuthRepository
import retrofit2.HttpException
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {

        return try {

            val response = api.login(
                LoginRequest(email, password)
            )
            val token    = response.data.token
            saveTokenAndClaims(token)
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
            val token    = response.data.token
            saveTokenAndClaims(token)
            Result.success(response.data.token)

        } catch (e: HttpException) {
            val message = parseError(e)
            Result.failure(Exception(message))

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
    private fun saveTokenAndClaims(token: String) {
        tokenManager.saveToken(token)
        try {
            val payload = token.split(".")[1]
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE))
            val json    = JSONObject(decoded)
            json.optString("sub", null)?.let  { tokenManager.saveUserEmail(it) }
            json.optString("name", null)?.let { tokenManager.saveUserName(it) }
            val id = json.optLong("id", -1L)
            if (id != -1L) tokenManager.saveUserId(id)
        } catch (e: Exception) {
            // Si falla la decodificación, el token igual se guarda
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