package com.ikseong.ucompass.data.network.service

import com.ikseong.ucompass.data.network.response.NaverGeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverService {
    @GET("map-reversegeocode/v2/gc")
    suspend fun reverseGeocode(
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String,
        @Query("coords") coords: String, // "경도,위도" 형식
        @Query("output") output: String = "json",
        @Query("orders") orders: String = "roadaddr"
    ): NaverGeocodingResponse
} 