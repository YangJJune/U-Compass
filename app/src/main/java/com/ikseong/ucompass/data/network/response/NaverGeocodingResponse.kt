package com.ikseong.ucompass.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NaverGeocodingResponse(
    @SerialName("status")
    val status: Status,
    @SerialName("results")
    val results: List<AddressResult>
)

@Serializable
data class Status(
    @SerialName("code")
    val code: Int,
    @SerialName("name")
    val name: String,
    @SerialName("message")
    val message: String
)

@Serializable
data class AddressResult(
    @SerialName("name")
    val name: String,
    @SerialName("code")
    val code: Code,
    @SerialName("region")
    val region: Region
)

@Serializable
data class Code(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: String,
    @SerialName("mappingId")
    val mappingId: String
)

@Serializable
data class Region(
    @SerialName("area0")
    val area0: Area,
    @SerialName("area1")
    val area1: Area,
    @SerialName("area2")
    val area2: Area,
    @SerialName("area3")
    val area3: Area,
    @SerialName("area4")
    val area4: Area
)

@Serializable
data class Area(
    @SerialName("name")
    val name: String,
    @SerialName("coords")
    val coords: Coords
)

@Serializable
data class Coords(
    @SerialName("center")
    val center: Center
)

@Serializable
data class Center(
    @SerialName("crs")
    val crs: String,
    @SerialName("x")
    val x: Double,
    @SerialName("y")
    val y: Double
) 