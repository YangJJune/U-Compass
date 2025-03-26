package com.example.composeimage.network

import com.example.composeimage.model.ImageUrlResponse
import retrofit2.http.GET

interface ImageService {
    @GET("/api/cache-data")
    suspend fun getImages(): ImageUrlResponse
}