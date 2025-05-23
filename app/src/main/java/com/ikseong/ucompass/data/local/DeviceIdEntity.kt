package com.ikseong.ucompass.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_id_table")
data class DeviceIdEntity(
    @PrimaryKey val id: Int = 0, // 항상 단 하나의 값만 저장
    val deviceId: String
)