package com.ikseong.ucompass.data.di

import com.ikseong.ucompass.data.local.UserDao
import com.ikseong.ucompass.data.local.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideDeviceIdDao(db: UserDatabase): UserDao = db.deviceIdDao()
}