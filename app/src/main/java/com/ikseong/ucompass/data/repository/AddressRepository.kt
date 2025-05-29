package com.ikseong.ucompass.data.repository

interface AddressRepository {
    /**
     * 좌표를 주소로 변환하는 역지오코딩 기능
     * @param latitude 위도
     * @param longitude 경도
     * @return 주소 문자열 또는 실패 시 null
     */
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): Result<String>
} 