package com.ikseong.ucompass.data.repository

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.network.request.JoinRoomRequest
import com.ikseong.ucompass.data.network.request.LeaveRoomRequest
import com.ikseong.ucompass.data.network.response.RoomDataResponse
import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomResultResponse
import com.ikseong.ucompass.data.network.response.RootResponse

interface UCompassRepository {
    suspend fun getRootApi(): Result<RootResponse>

    suspend fun getRoomList(): Result<List<RoomItemResponse>>

    suspend fun getRoomItem(id: Long): Result<RoomItemResponse>

    suspend fun createRoom(request: CreateRoomRequest): Result<RoomDataResponse>

    suspend fun deleteRoom(id: Int): Result<Unit>

    suspend fun joinRoom(request: JoinRoomRequest): Result<RoomResultResponse>

    suspend fun leaveRoom(request: LeaveRoomRequest): Result<RoomResultResponse>
}