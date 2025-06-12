package com.ikseong.ucompass.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEditRequest(
    @SerialName("email")
    val email: String,
    @SerialName("profileImage")
    val profileImage: String,
    @SerialName("userName")
    val userName: String
)