package com.ikseong.ucompass.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.GetUserNameUseCase
import com.ikseong.ucompass.domain.SaveUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserNameUseCase: SaveUserNameUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

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

    fun saveName() {
        viewModelScope.launch {
            saveUserNameUseCase(uiState.value.name)
        }
    }
}