package com.ikseong.ucompass.domain

import com.ikseong.ucompass.data.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveUserNameUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(name: String) = repository.saveUserName(name)
}