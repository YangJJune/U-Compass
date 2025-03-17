package com.example.composenavigation.simple

import kotlinx.serialization.Serializable

sealed class Simple {

    @Serializable
    data object Home : Simple()

    @Serializable
    data class Profile(val name: String) : Simple()
}
