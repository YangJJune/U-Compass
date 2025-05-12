package com.ikseong.ucompass.ui.common.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import kotlinx.collections.immutable.ImmutableList

/**
 * 네이버 지도에 표시할 마커 정보
 */
data class MapMarker(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isVisible: Boolean = true,
    val distanceText: String? = null
)

/**
 * 네이버 지도 컴포넌트
 *
 * @param markers 지도에 표시할 마커 정보 리스트
 * @param currentLocation 현재 위치 (위도, 경도)
 * @param cameraPositionState 카메라 위치 상태 (외부에서 관리)
 * @param onMapClick 지도 클릭 이벤트 콜백
 * @param modifier Modifier
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapComponent(
    modifier: Modifier = Modifier,
    markers: ImmutableList<MapMarker>,
    currentLocation: LatLng = LatLng(37.5666805, 126.9784147), // 서울 중심 기본값
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapClick: (LatLng) -> Unit = {},
) {
    cameraPositionState.position = CameraPosition(LatLng(
        currentLocation.latitude + 0.0045,
        currentLocation.longitude
    ), 15.0)

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isIndoorEnabled = true,
            isBuildingLayerGroupEnabled = true,
        ),
        uiSettings = MapUiSettings(
            isLocationButtonEnabled = true,
            isZoomControlEnabled = false,
            isCompassEnabled = true,
        ),
        onMapClick = { _, latLng ->
            onMapClick(latLng)
        }
    ) {
        // 현재 위치 마커
        Marker(
            state = MarkerState(position = currentLocation),
            captionText = "현재 위치"
        )

        // 마커 표시
        markers.forEach { marker ->
            if (marker.isVisible) {
                val markerLocation = LatLng(marker.latitude, marker.longitude)
                val captionText = if (marker.distanceText != null) {
                    "${marker.name} (${marker.distanceText})"
                } else {
                    marker.name
                }

                Marker(
                    state = MarkerState(position = markerLocation),
                    captionText = captionText
                )
            }
        }
    }
} 