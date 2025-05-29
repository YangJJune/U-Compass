package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveDeviceIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(deviceId: String) = repository.setDeviceId(deviceId)
}