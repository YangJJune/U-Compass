package com.ikseong.ucompass.ui.common.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.model.DistanceType
import com.ikseong.ucompass.ui.model.DistanceType.Companion.fromDistance
import com.ikseong.ucompass.ui.room.component.ParticipantPin
import com.ikseong.ucompass.ui.util.MapParticipantUtil.getCardinalDirectionFromRelative
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.overlay.OverlayImage

/**
 * 네이버 지도에 표시할 마커 정보
 */
data class MapMarker(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isVisible: Boolean = true,
    val distance: Int = 0,
    val type: DistanceType,
    var angle: Float = 0f
) {
    constructor(
        name: String,
        latitude: Double,
        longitude: Double,
        isVisible: Boolean = true,
        distance: Int = 0
    ) : this(
        name = name,
        latitude = latitude,
        longitude = longitude,
        isVisible = isVisible,
        distance = distance,
        type = fromDistance(distance),
    )
}

/**
 * 네이버 지도 컴포넌트
 *
 * @param markers 지도에 표시할 마커 정보 리스트
 * @param currentLocation 현재 위치 (위도, 경도)
 * @param cameraPositionState 카메라 위치 상태 (외부에서 관리)
 * @param deviceOrientation 기기 방향 (도 단위)
 * @param onMapClick 지도 클릭 이벤트 콜백
 * @param modifier Modifier
 * @param uiSettings 지도 UI 설정 콜백
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapComponent(
    modifier: Modifier = Modifier,
    markers: List<MapMarker>,
    isMapVisible: Boolean,
    currentLocation: LatLng,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapClick: (LatLng) -> Unit = {},
    uiSettings: MapUiSettings = MapUiSettings(),
) {
    val properties = if (isMapVisible) {
        MapProperties(
            isBuildingLayerGroupEnabled = true,
            locationTrackingMode = LocationTrackingMode.Face, // Face 모드로 변경 (위치와 방향 모두 추적)
            isLiteModeEnabled = true,
        )
    } else {
        MapProperties(
            isBuildingLayerGroupEnabled = true,
            locationTrackingMode = LocationTrackingMode.Face, // Face 모드로 변경 (위치와 방향 모두 추적)
            isLiteModeEnabled = true,
            isIndoorEnabled = true,
            lightness = -1f,
            backgroundColor = Color.Transparent,
        )
    }

    NaverMap(
        modifier = modifier.fillMaxWidth(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = { _, latLng ->
            onMapClick(latLng)
        },
    ) {
        // 현재 위치 마커
        Marker(
            width = 60.dp,
            height = 60.dp,
            state = MarkerState(position = currentLocation),
            icon = OverlayImage.fromResource(R.drawable.ic_user_direction),
            captionText = "현재 위치"
        )

        // 마커 표시
        markers.forEach { marker ->
            if (marker.isVisible) {
                val markerLocation = LatLng(marker.latitude, marker.longitude)
                val captionText = "${marker.name} (${marker.distance})"
                if (marker.distance >= 400) {
                    Marker(
                        state = MarkerState(position = markerLocation),
                        captionText = captionText
                    )
                } else {
                    val direction = getCardinalDirectionFromRelative(marker.angle.toDouble())

                    MarkerComposable(
                        state = MarkerState(position = markerLocation),
                        captionText = captionText
                    ) {
                        ParticipantPin(
                            name = marker.name,
                            isMapVisible = isMapVisible,
                            direction = direction,
                            distance = marker.distance,
                            type = marker.type
                        )
                    }
                }
            }
        }
    }
}
