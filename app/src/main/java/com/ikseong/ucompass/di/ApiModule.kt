package com.ikseong.ucompass.di

import com.ikseong.ucompass.data.service.UCompassService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideUCompassService(retrofit: Retrofit): UCompassService = retrofit.create()

}