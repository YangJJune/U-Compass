package com.ikseong.ucompass.data.service

import retrofit2.http.GET

interface UCompassService {
    @GET("/")
    fun getRootApi()

}