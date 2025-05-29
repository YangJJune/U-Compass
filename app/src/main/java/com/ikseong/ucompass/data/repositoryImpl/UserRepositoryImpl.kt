package com.ikseong.ucompass.data.repositoryImpl

import com.ikseong.ucompass.data.local.DeviceIdEntity
import com.ikseong.ucompass.data.local.UserDao
import com.ikseong.ucompass.data.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getDeviceId() = userDao.getDeviceId()

    override suspend fun setDeviceId(deviceId: String) {
        getDeviceId().collect {
            it ?: run {
                userDao.saveDeviceId(DeviceIdEntity(id = 0, deviceId = deviceId))
            }
        }
    }

    override suspend fun saveUserName(name: String) {

    }
}