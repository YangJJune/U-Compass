package com.ikseong.ucompass.data.repository

import com.ikseong.ucompass.data.network.response.RootResponse

interface UCompassRepository {
    suspend fun getRootApi(): Result<RootResponse>
}