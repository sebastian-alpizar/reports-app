package com.example.mobile.core.util


import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "jwt"
        private const val IS_ADMIN = "is_admin"
    }

    fun saveToken(token: String) =
        prefs.edit().putString(TOKEN_KEY, token).apply()

    fun getToken(): String? =
        prefs.getString(TOKEN_KEY, null)

    fun clear() =
        prefs.edit().clear().apply()

    // ── JWT claims ──
    fun getUserEmail(): String? =
        getToken()?.let { decodeJwtClaim(it, "sub") }

    fun getUserName(): String? =
        getToken()?.let { decodeJwtClaim(it, "name") }

    fun getUserId(): Long? =
        getToken()?.let { decodeJwtClaim(it, "id")?.toLongOrNull() }

    private fun decodeJwtClaim(token: String, claim: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(
                android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
            )
            val json = org.json.JSONObject(decoded)
            json.optString(claim, null)
        } catch (e: Exception) {
            null
        }
    }

    // ── ADMIN ──
    fun saveIsAdmin(isAdmin: Boolean) {
        prefs.edit().putBoolean(IS_ADMIN, isAdmin).apply()
    }

    fun isAdmin(): Boolean {
        return getToken()?.let {
            decodeJwtClaim(it, "is_admin")?.toBooleanStrictOrNull()
                ?: decodeJwtClaim(it, "is_admin")?.equals("true", ignoreCase = true)
        } ?: false
    }
}