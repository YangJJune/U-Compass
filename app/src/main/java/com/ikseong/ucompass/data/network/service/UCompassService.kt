package com.ikseong.ucompass.data.network.service

import com.ikseong.ucompass.data.network.response.RootResponse
import retrofit2.http.GET

interface UCompassService {
    @GET("/")
    fun getRootApi(): RootResponse

}