package com.ikseong.ucompass.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.main.viewmodel.RoomInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun MainRoomList(
    modifier: Modifier = Modifier,
    roomList: List<RoomInfo>,
    onRoomClick: (Long) -> Unit = {},
) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 21.dp),
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {

    }
}

@Composable
private fun MainRoomItem(
    modifier: Modifier = Modifier,
    roomInfo: RoomInfo,
    onRoomClick: (Long) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0x8000E397),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onRoomClick(0) } // Assuming room ID is 0 for now, replace with actual room ID
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