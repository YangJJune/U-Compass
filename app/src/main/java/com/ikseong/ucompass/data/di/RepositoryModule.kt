package com.ikseong.ucompass.data.di

import com.ikseong.ucompass.data.repository.UCompassRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUCompassRepository(
        uCompassRepository: UCompassRepository
    ): UCompassRepository

}