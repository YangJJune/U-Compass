package com.example.composenavigation.nested

import kotlinx.serialization.Serializable

@Serializable
open class Nested {

    @Serializable
    data object Onboarding : Nested()

    @Serializable
    data object Landing : Nested()

    @Serializable
    data object Login : Nested()
}