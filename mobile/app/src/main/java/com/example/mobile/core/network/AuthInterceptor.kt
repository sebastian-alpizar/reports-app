package com.example.mobile.core.network

import com.example.mobile.core.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val token = tokenManager.getToken()

        val newRequest = request.newBuilder()

        token?.let {
            newRequest.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(newRequest.build())
    }
}