package com.ikseong.ucompass.data.repositoryImpl

import android.util.Log
import com.ikseong.ucompass.BuildConfig
import com.ikseong.ucompass.data.network.service.NaverService
import com.ikseong.ucompass.data.repository.AddressRepository
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val naverService: NaverService
) : AddressRepository {

    override suspend fun getAddressFromCoordinates(
        latitude: Double, 
        longitude: Double
    ): Result<String> = runCatching {
        val coords = "$longitude,$latitude" // 네이버 API는 경도,위도 순서
        Log.d("AddressRepository", "Requesting address for coords: $coords")
        val response = naverService.reverseGeocode(
            clientId = BuildConfig.NAVER_MAP_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_MAP_CLIENT_SECRET,
            coords = coords
        )

        // 성공적인 응답인지 확인
        if (response.status.code == 0 && response.results.isNotEmpty()) {
            val result = response.results.first()
            val region = result.region
            
            // 주소 구성: 시/도 + 시/군/구 + 읍/면/동
            buildString {
                if (region.area1.name.isNotEmpty()) {
                    append(region.area1.name)
                }
                if (region.area2.name.isNotEmpty()) {
                    if (isNotEmpty()) append(" ")
                    append(region.area2.name)
                }
                if (region.area3.name.isNotEmpty()) {
                    if (isNotEmpty()) append(" ")
                    append(region.area3.name)
                }
            }.ifEmpty { "주소를 찾을 수 없습니다" }
        } else {
            throw Exception("네이버 API 응답 오류: ${response.status.message}")
        }
    }
} 