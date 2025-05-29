package com.ikseong.ucompass.data.repository

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getDeviceId(): Flow<String?>

    suspend fun setDeviceId(deviceId: String)

    suspend fun saveUserName(name: String)
}