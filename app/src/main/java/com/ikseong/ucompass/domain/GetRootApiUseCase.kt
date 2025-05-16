package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRootApiUseCase @Inject constructor(
    private val repository: UCompassRepository,
) {
    suspend operator fun invoke() = repository.getRootApi()
}