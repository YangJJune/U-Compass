package com.ikseong.ucompass.data.network.service

import retrofit2.http.GET

interface UCompassService {
    @GET("/")
    fun getRootApi()

}