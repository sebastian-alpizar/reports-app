package com.example.mobile.core.util


import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("jwt", token).apply()
    fun getToken(): String? = prefs.getString("jwt", null)

    fun saveUserId(id: Long) {
        prefs.edit().putLong("user_id", id).apply()
    }

    fun getUserId(): Long? {
        val id = prefs.getLong("user_id", -1L)
        return if (id == -1L) null else id
    }

    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String? = prefs.getString("user_name", null)

    fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? = prefs.getString("user_email", null)

    fun clear() = prefs.edit().clear().apply()

    // ── Nuevo: extraer claims del JWT sin librería extra ──
//    fun getUserEmail(): String? = getToken()?.let { decodeJwtClaim(it, "sub") }
//    fun getUserName(): String? = getToken()?.let { decodeJwtClaim(it, "name") }

//    fun getUserId(): Long? = getToken()?.let { decodeJwtClaim(it, "id")?.toLongOrNull() }

    private fun decodeJwtClaim(token: String, claim: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE))
            val json = org.json.JSONObject(decoded)
            json.optString(claim, null)
        } catch (e: Exception) {
            null
        }
    }
}