package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDeviceIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke() = repository.getDeviceId()
}