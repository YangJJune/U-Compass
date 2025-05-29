package com.ikseong.ucompass.ui.onboarding.viewmodel

import android.net.Uri


data class OnboardingUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUri : Uri = Uri.EMPTY,
    val isSuccess: Boolean = false,
)
