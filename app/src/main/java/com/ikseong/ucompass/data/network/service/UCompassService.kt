package com.ikseong.ucompass.data.network.service

import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.data.network.response.RootResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UCompassService {
    @GET("/")
    suspend fun getRootApi(): RootResponse

    @GET("/rooms")
    suspend fun getRoomList(): RoomListResponse

    @GET("/rooms/{room_id}")
    suspend fun getRoomItem(
        @Path("room_id") roomId: Long
    ): RoomItemResponse
}