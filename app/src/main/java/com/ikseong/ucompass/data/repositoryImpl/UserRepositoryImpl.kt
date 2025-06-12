package com.ikseong.ucompass.data.repositoryImpl

import com.ikseong.ucompass.data.local.UserPreferences
import com.ikseong.ucompass.data.network.request.UserEditRequest
import com.ikseong.ucompass.data.network.request.UserRegisterRequest
import com.ikseong.ucompass.data.network.service.UCompassService
import com.ikseong.ucompass.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val service: UCompassService,
) : UserRepository {

    override fun getDeviceId(): Flow<String?> = userPreferences.getDeviceId()

    override suspend fun setDeviceId(deviceId: String) {
        // 디바이스 ID가 없을 경우에만 저장
        val existingId = getDeviceId().firstOrNull()
        if (existingId == null) {
            userPreferences.saveDeviceId(deviceId)
        }
    }

    override fun getUserName(): Flow<String?> = userPreferences.getUserName()

    override suspend fun saveUserName(name: String) {
        userPreferences.saveUserName(name)
    }

    override suspend fun registerUser(
        name: String,
        deviceId: String
    ): Result<Unit> = runCatching {
        val requestBody = UserRegisterRequest(
            deviceId = deviceId,
            email = "",
            profileImage = "",
            userName = name
        )
        service.registerUser(requestBody)
    }

    override suspend fun editUser(
        name: String,
        deviceId: String
    ): Result<Unit> = runCatching {
        val requestBody = UserEditRequest(
            email = "",
            profileImage = "",
            userName = name
        )
        service.editUser(deviceId, requestBody)
    }

}