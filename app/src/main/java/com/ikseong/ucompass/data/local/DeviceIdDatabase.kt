package com.ikseong.ucompass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DeviceIdEntity::class], version = 1)
abstract class DeviceIdDatabase : RoomDatabase() {
    abstract fun deviceIdDao(): DeviceIdDao
}