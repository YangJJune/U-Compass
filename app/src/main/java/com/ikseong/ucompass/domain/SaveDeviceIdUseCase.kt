package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.DeviceIdRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveDeviceIdUseCase @Inject constructor(
    private val repository: DeviceIdRepository,
) {
    suspend operator fun invoke(deviceId: String) = repository.setDeviceId(deviceId)
}