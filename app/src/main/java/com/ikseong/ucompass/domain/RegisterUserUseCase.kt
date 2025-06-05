package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        name: String,
        deviceId: String
    ) = repository.registerUser(
        name = name,
        deviceId = deviceId
    )
}