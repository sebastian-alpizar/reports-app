//package com.example.mobile.core.di
//
//import android.content.Context
//import com.example.mobile.data.repository.LocationRepositoryImpl
//import com.example.mobile.domain.repository.LocationRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object LocationModule {
//
//    @Provides
//    @Singleton
//    fun provideLocationRepository(
//        @ApplicationContext context: Context
//    ): LocationRepository {
//        return LocationRepositoryImpl(context)
//    }
//}