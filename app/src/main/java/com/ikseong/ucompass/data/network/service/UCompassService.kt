package com.ikseong.ucompass.data.network.service

import com.ikseong.ucompass.data.network.request.CreateRoomRequest
import com.ikseong.ucompass.data.network.request.JoinRoomRequest
import com.ikseong.ucompass.data.network.request.LeaveRoomRequest
import com.ikseong.ucompass.data.network.request.UserEditRequest
import com.ikseong.ucompass.data.network.request.UserRegisterRequest
import com.ikseong.ucompass.data.network.response.RoomDataResponse
import com.ikseong.ucompass.data.network.response.RoomItemResponse
import com.ikseong.ucompass.data.network.response.RoomResultResponse
import com.ikseong.ucompass.data.network.response.RootResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UCompassService {
    @GET("/")
    suspend fun getRootApi(): RootResponse

    @GET("/rooms")
    suspend fun getRoomList(): List<RoomItemResponse>

    @GET("/room/{room_id}")
    suspend fun getRoomItem(
        @Path("room_id") roomId: Long
    ): RoomItemResponse

    @POST("/room/create")
    suspend fun createRoom(
        @Body request: CreateRoomRequest
    ): RoomDataResponse

    @DELETE("/room/{room_id}")
    suspend fun deleteRoom(
        @Path("room_id") roomId: Int
    ): Unit

    @POST("/room/join")
    suspend fun joinRoom(
        @Body request: JoinRoomRequest
    ): RoomResultResponse

    @POST("/room/leave")
    suspend fun leaveRoom(
        @Body request: LeaveRoomRequest
    ): RoomResultResponse

    @POST("/user")
    suspend fun registerUser(
        @Body request: UserRegisterRequest
    ): Unit

    @PATCH("/user/{device_id}")
    suspend fun editUser(
        @Path("device_id") deviceId: String,
        @Body request: UserEditRequest
    ): Unit
}