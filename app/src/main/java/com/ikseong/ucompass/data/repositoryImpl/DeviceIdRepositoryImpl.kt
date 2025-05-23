package com.ikseong.ucompass.data.repositoryImpl

import com.ikseong.ucompass.data.local.DeviceIdDao
import com.ikseong.ucompass.data.local.DeviceIdEntity
import com.ikseong.ucompass.data.repository.DeviceIdRepository
import javax.inject.Inject

class DeviceIdRepositoryImpl @Inject constructor(
    private val deviceIdDao: DeviceIdDao
) : DeviceIdRepository {

    override fun getDeviceId() = deviceIdDao.getDeviceId()

    override suspend fun setDeviceId(deviceId: String) {
        getDeviceId().collect {
            it ?: run {
                deviceIdDao.saveDeviceId(DeviceIdEntity(id = 0, deviceId = deviceId))
            }
        }
    }
}