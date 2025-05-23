package com.ikseong.ucompass.data.di

import android.content.Context
import androidx.room.Room
import com.ikseong.ucompass.data.local.DeviceIdDao
import com.ikseong.ucompass.data.local.DeviceIdDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DeviceIdDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DeviceIdDatabase::class.java,
            "device_id_db"
        ).build()
    }

}
