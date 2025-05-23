package com.ikseong.ucompass.data.repository

import kotlinx.coroutines.flow.Flow

interface DeviceIdRepository {
    suspend fun getDeviceId(): Flow<String>

    suspend fun setDeviceId(deviceId: String)
}