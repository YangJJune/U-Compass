package com.ikseong.ucompass.data.repository

import kotlinx.coroutines.flow.Flow

interface DeviceIdRepository {
    fun getDeviceId(): Flow<String?>

    suspend fun setDeviceId(deviceId: String)
}