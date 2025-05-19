package com.ikseong.ucompass.data.repository

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.network.response.CreateRoomResponse
import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.data.network.response.RootResponse

interface UCompassRepository {
    suspend fun getRootApi(): Result<RootResponse>

    suspend fun getRoomList(): Result<List<RoomListResponse.RoomListResponseItem>>

    suspend fun getRoomItem(id: Long): Result<RoomItemResponse>

    suspend fun createRoom(request: CreateRoomRequest): Result<CreateRoomResponse>

    suspend fun deleteRoom(id: Int): Result<Unit>
}