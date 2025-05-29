package com.ikseong.ucompass.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {
    @Serializable
    data object Home: Routes

    @Serializable
    data object CreateRoomTitle : Routes
    @Serializable
    data object CreateRoomFinished : Routes

    @Serializable
    data class Room(val id: Long): Routes

    @Serializable
    data object Onboarding: Routes

    @Serializable
    data object OnboardingInsert : Routes
}