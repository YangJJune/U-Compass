package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.MapMarker
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.DistanceType
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import com.naver.maps.geometry.LatLng
import kotlin.math.roundToInt

@Composable
fun RoomSearchContent(
    modifier: Modifier = Modifier,
    address: String,
    isMapVisible: Boolean = false,
    myLocation: LatLng? = null,
    mapMarkers: List<MapMarker> = emptyList(),
    updateWidthHeight: (Int, Int) -> Unit = { _, _ -> }
) {
    var widthPx by remember { mutableIntStateOf(0) }
    var heightPx by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(
                    color = if (isMapVisible) Color.White else Color.White.copy(alpha = 0.8f)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_main),
                contentDescription = null,
                tint = Color(0xFF606060)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = address,
                style = typography.medium.copy(
                    fontSize = 16.sp,
                    color = Color(0xFF606060)
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    widthPx = coordinates.size.width
                    heightPx = coordinates.size.height
                    updateWidthHeight(widthPx, heightPx)
                }
        ) {
            mapMarkers.forEach { mapMarker ->
                if (mapMarker.isVisible && mapMarker.distance >= 90) {
                    myLocation?.let {

                        Box(
                            modifier = Modifier
                                .align(
                                    when (mapMarker.alignDirection) {
                                        Direction.N -> Alignment.TopCenter
                                        Direction.E -> Alignment.CenterEnd
                                        Direction.S -> Alignment.BottomCenter
                                        Direction.W -> Alignment.CenterStart
                                        else -> Alignment.TopCenter
                                    }
                                )
                                .then(
                                    when (mapMarker.alignDirection) {
                                        Direction.N -> Modifier.offset {
                                            IntOffset(
                                                mapMarker.padding.roundToInt(),
                                                0
                                            )
                                        }

                                        Direction.E -> Modifier.offset {
                                            IntOffset(
                                                0,
                                                mapMarker.padding.roundToInt()
                                            )
                                        }

                                        Direction.S -> Modifier.offset {
                                            IntOffset(
                                                mapMarker.padding.roundToInt(),
                                                0
                                            )
                                        }

                                        else -> Modifier.offset {
                                            IntOffset(
                                                0,
                                                mapMarker.padding.roundToInt()
                                            )
                                        }
                                    }
                                )
                                .background(Color.Transparent)
                        ) {
                            ParticipantPin(
                                name = mapMarker.name,
                                isMapVisible = isMapVisible,
                                direction = mapMarker.pinDirection,
                                distance = mapMarker.distance,
                                type = mapMarker.type
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RoomSearchContentPreview() {
    RoomSearchContent(
        address = "123 Main St, City, Country",
        isMapVisible = true,
        myLocation = LatLng(37.5665, 126.978),
        mapMarkers = listOf(
            MapMarker(
                latitude = 39.8665,
                longitude = 129.978,
                name = "홍길동",
                type = DistanceType.FIVE_HUNDRED,
                distance = 2000,
                isVisible = true
            ),
            MapMarker(
                latitude = 37.5670,
                longitude = 127.479,
                name = "김철수",
                type = DistanceType.TWO_THOUSAND,
                distance = 1000,
                isVisible = true
            ),
            MapMarker(
                latitude = 37.5664,
                longitude = 126.977,
                name = "박창수",
                type = DistanceType.TWO_THOUSAND,
                distance = 400,
                isVisible = true
            )
        )
    )
}