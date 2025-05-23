package com.ikseong.ucompass.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceIdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDeviceId(deviceId: DeviceIdEntity)

    @Query("SELECT deviceId FROM device_id_table WHERE id = 0")
    fun getDeviceId(): Flow<String?>
}