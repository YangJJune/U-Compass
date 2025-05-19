package com.ikseong.ucompass.data.repository

import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.data.network.response.RootResponse

interface UCompassRepository {
    suspend fun getRootApi(): Result<RootResponse>

    suspend fun getRoomList(): Result<List<RoomListResponse.RoomListResponseItem>>

    suspend fun getRoomItem(id: Long) : Result<RoomItemResponse>
}