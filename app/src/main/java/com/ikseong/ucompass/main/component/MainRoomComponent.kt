package com.ikseong.ucompass.main.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.main.viewmodel.RoomInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MainRoomList(
    modifier: Modifier = Modifier,
    roomList: List<RoomInfo>,
    onRoomClick: (Long) -> Unit = {},
    onActionClick: (Long) -> Unit = {}
) {
    val density = LocalDensity.current
    val actionWidth = remember { with(density) { 70.dp.toPx() } }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.padding(horizontal = 21.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(roomList.size) { index ->
            MainRoomItemWithAction(
                modifier = Modifier,
                roomInfo = roomList[index],
                actionWidth = actionWidth,
                scope = scope,
                onActionClick = {
                    onActionClick(roomList[index].roomId)
                },
                onRoomClick = {
                    onRoomClick(roomList[index].roomId)
                }
            )
        }
    }
}

@Composable
private fun MainRoomItemWithAction(
    modifier: Modifier = Modifier,
    roomInfo: RoomInfo,
    actionWidth: Float,
    scope: CoroutineScope,
    onActionClick: () -> Unit = {},
    onRoomClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF00E397))
                .align(Alignment.CenterEnd)
                .clickable { onActionClick() }
        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(
                    if (roomInfo.isHost) R.drawable.ic_main_room_delete
                    else R.drawable.ic_main_room_out
                ),
                contentDescription = "방 나가기 및 삭제"
            )
        }
        MainRoomItem(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            roomInfo = roomInfo,
            scope = scope,
            actionWidth = actionWidth,
            onRoomClick = onRoomClick
        )
    }
}

@Composable
private fun MainRoomItem(
    modifier: Modifier = Modifier,
    roomInfo: RoomInfo,
    scope: CoroutineScope,
    actionWidth: Float = 0f,
    onRoomClick: () -> Unit = {}
) {
    val offset = remember {
        Animatable(initialValue = 0f)
    }
    Row(
        modifier = modifier
            .offset { IntOffset(offset.value.roundToInt(), 0) }
            .clickable { onRoomClick() }
            .pointerInput(actionWidth) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        scope.launch {
                            val newOffset = (offset.value + dragAmount)
                                .coerceIn(-actionWidth, 0f)
                            offset.snapTo(newOffset)
                        }
                    },
                    onDragEnd = {
                        if (offset.value < -actionWidth / 2) {
                            scope.launch {
                                offset.animateTo(-actionWidth)
                            }
                        } else {
                            scope.launch {
                                offset.animateTo(0f)
                            }
                        }
                    }
                )
            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0x8000E397),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(77.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color(0xFFCCF9EA))
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    painter = painterResource(R.drawable.ic_user_info_main),
                    contentDescription = "Default Image",
                    tint = Color(0xFF00E397)
                )
            }

            MainRoomInfo(
                modifier = Modifier.padding(start = 12.dp),
                roomName = roomInfo.roomName,
                hostName = roomInfo.hostName,
                roomLink = roomInfo.roomLink
            )
        }
        if (roomInfo.isHost) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                painter = painterResource(R.drawable.ic_crown_main),
                contentDescription = "crown",
                tint = Color(0xFF00E397)
            )
        }
    }
}


@Composable
private fun MainRoomInfo(
    modifier: Modifier = Modifier,
    roomName: String = "",
    hostName: String = "",
    roomLink: String = "",
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = roomName,
            style = typography.medium.copy(
                fontSize = 18.sp,
                lineHeight = 20.sp
            )
        )
        Row(
            modifier = Modifier.padding(top = 14.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                painter = painterResource(R.drawable.ic_user_info_main),
                contentDescription = "방장 이름",
                tint = Color(0xFF606060)
            )

            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                text = hostName,
                style = typography.medium.copy(
                    fontSize = 14.sp,
                    color = Color(0x80000000),
                    lineHeight = 20.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 9.dp)
                .clickable { /*TODO: 복사 or 공유*/ }
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                painter = painterResource(R.drawable.ic_create_finish_link),
                contentDescription = "방 번호",
                tint = Color(0xFF606060)
            )

            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                text = roomLink,
                style = typography.medium.copy(
                    fontSize = 14.sp,
                    color = Color(0x80000000),
                    lineHeight = 20.sp
                ),
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainRoomItemPreview() {
    MainRoomItem(
        scope = rememberCoroutineScope(),
        roomInfo = RoomInfo(
            hostName = "HostName",
            roomLink = "RoomLink",
            roomName = "RoomName",
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MainRoomListPreview() {
    MainRoomList(
        modifier = Modifier,
        roomList = listOf(
            RoomInfo(
                hostName = "HostName1",
                roomLink = "RoomLink1",
                roomName = "RoomName1"
            ),
            RoomInfo(
                hostName = "HostName2",
                roomLink = "RoomLink2",
                roomName = "RoomName2"
            )
        ),
        onRoomClick = { roomId ->
            // Handle room click
        }
    )
}