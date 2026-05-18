package com.example.mobile.core.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs =
        context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}