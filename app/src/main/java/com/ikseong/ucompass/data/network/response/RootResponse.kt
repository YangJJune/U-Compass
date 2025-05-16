package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RootResponse(
    @SerialName("message")
    val message: String
)