package com.example.composenavigation.capsuled

import kotlinx.serialization.Serializable

@Serializable
sealed class Capsuled {

    @Serializable
    data object Onboarding : Capsuled()

    @Serializable
    data object Landing : Capsuled()

    @Serializable
    data object Login : Capsuled()
}