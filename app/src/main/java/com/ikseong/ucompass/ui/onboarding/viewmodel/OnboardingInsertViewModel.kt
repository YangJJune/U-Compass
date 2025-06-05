package com.ikseong.ucompass.ui.onboarding.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.GetDeviceIdUseCase
import com.ikseong.ucompass.domain.RegisterUserUseCase
import com.ikseong.ucompass.domain.SaveDeviceIdUseCase
import com.ikseong.ucompass.domain.SaveUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class OnboardingInsertViewModel @Inject constructor(
    private val saveUserNameUseCase: SaveUserNameUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val saveDeviceIdUseCase: SaveDeviceIdUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    private val _deviceId = MutableStateFlow("")


    @OptIn(ExperimentalUuidApi::class)
    private fun saveDeviceId() {
        val uuid = Uuid.random().toString()

        viewModelScope.launch {
            saveDeviceIdUseCase(uuid)
            getDeviceIdUseCase().collect { deviceId ->
                if (deviceId != null) {
                    _deviceId.value = deviceId
                } else {
                    _deviceId.value = uuid
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun register() {
        saveName()
        saveDeviceId()

        viewModelScope.launch {
            val deviceId = _deviceId.first()
            registerUserUseCase(name = uiState.value.name, deviceId = deviceId.toString()).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(isSuccess = true)
                    }
                },
                onFailure = { e ->
                    Log.e("OnboardingInsertViewModel", "User registration failed: ${e.message}")
                }
            )
        }
    }

    fun saveName() {
        viewModelScope.launch {
            saveUserNameUseCase(uiState.value.name)
            _uiState.update {
                it.copy(isSuccess = true)
            }
        }
    }
}