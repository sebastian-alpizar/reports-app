package com.example.mobile.core.di

import com.example.mobile.data.remote.api.AuthApi
import com.example.mobile.core.network.AuthInterceptor
import com.example.mobile.data.remote.api.NotificationApi
import com.example.mobile.data.remote.api.ReportApi
import com.example.mobile.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Para local
    //private const val BASE_URL = "http://10.0.2.2:8080/api/"

    // Para hosting
    private const val BASE_URL = "https://reports-app-how0.onrender.com/api/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)   // Tiempo para conectar
            .writeTimeout(30, TimeUnit.SECONDS)     // Tiempo para enviar datos
            .readTimeout(60, TimeUnit.SECONDS)      // Tiempo para recibir respuesta
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(
        retrofit: Retrofit
    ): ReportApi {
        return retrofit.create(ReportApi::class.java)
    }


    @Provides
    @Singleton
    fun provideNotificationApi(
        retrofit: Retrofit
    ): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
}