package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    @SerialName("deviceId")
    val deviceId: String,
    @SerialName("email")
    val email: String,
    @SerialName("profileImage")
    val profileImage: String,
    @SerialName("userName")
    val userName: String
)