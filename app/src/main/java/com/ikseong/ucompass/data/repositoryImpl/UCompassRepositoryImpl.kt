package com.ikseong.ucompass.data.repositoryImpl

import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.data.network.service.UCompassService
import com.ikseong.ucompass.data.repository.UCompassRepository
import javax.inject.Inject

class UCompassRepositoryImpl @Inject constructor(
    private val service: UCompassService
) : UCompassRepository {
    override suspend fun getRootApi() = runCatching {
        service.getRootApi()
    }

    override suspend fun getRoomList(): Result<List<RoomListResponse.RoomListResponseItem>> =
        runCatching {
            service.getRoomList()
        }
}