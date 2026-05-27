package com.example.mobile.data.remote.util

import org.json.JSONObject
import retrofit2.HttpException

object ErrorParser {

    fun parseError(e: HttpException): String {
        return try {

            val errorBody = e.response()
                ?.errorBody()
                ?.string()

            val json = JSONObject(errorBody ?: "")

            json.getString("message")

        } catch (ex: Exception) {
            "Error del servidor"
        }
    }
}