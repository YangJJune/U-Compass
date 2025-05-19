package com.ikseong.ucompass.data.network.service

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.network.response.CreateRoomResponse
import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomListResponse
import com.ikseong.ucompass.data.network.response.RootResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("/rooms")
    suspend fun createRoom(
        @Body request: CreateRoomRequest
    ): CreateRoomResponse

    @DELETE("/rooms/{room_id}")
    suspend fun deleteRoom(
        @Path("room_id") roomId: Int
    ): Unit
}