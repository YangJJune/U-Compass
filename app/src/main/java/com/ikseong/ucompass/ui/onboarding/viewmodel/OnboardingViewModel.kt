package com.ikseong.ucompass.ui.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ikseong.ucompass.domain.GetUserNameUseCase
import com.ikseong.ucompass.domain.SaveDeviceIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
) : ViewModel() {

    private val _hasUserName = MutableStateFlow(false)
    val hasUserName = _hasUserName.asStateFlow()

    init {

        loadUserName()
    }

    fun loadUserName() {
        viewModelScope.launch {
            getUserNameUseCase().first()?.let {
                _hasUserName.value = true
            }
        }
    }
}