package com.ikseong.ucompass.data.repositoryImpl

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.network.request.JoinRoomRequest
import com.ikseong.ucompass.data.network.request.LeaveRoomRequest
import com.ikseong.ucompass.data.network.response.RoomDataResponse
import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomResultResponse
import com.ikseong.ucompass.data.network.service.UCompassService
import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject

class UCompassRepositoryImpl @Inject constructor(
    private val service: UCompassService
) : UCompassRepository {
    override suspend fun getRootApi() = runCatching {
        service.getRootApi()
    }

    override suspend fun getRoomList(): Result<List<RoomItemResponse>> =
        runCatching {
            service.getRoomList()
        }

    override suspend fun getRoomItem(id: Long): Result<RoomItemResponse> =
        runCatching {
            service.getRoomItem(roomId = id)
        }

    override suspend fun createRoom(
        request: CreateRoomRequest
    ): Result<RoomDataResponse> =
        runCatching {
            service.createRoom(request = request)
        }

    override suspend fun deleteRoom(id: Int): Result<Unit> = runCatching {
        service.deleteRoom(roomId = id)
    }

    override suspend fun joinRoom(request: JoinRoomRequest): Result<RoomResultResponse> =
        runCatching {
            service.joinRoom(request = request)
        }

    override suspend fun leaveRoom(request: LeaveRoomRequest): Result<RoomResultResponse> =
        runCatching {
            service.leaveRoom(request = request)
        }

}