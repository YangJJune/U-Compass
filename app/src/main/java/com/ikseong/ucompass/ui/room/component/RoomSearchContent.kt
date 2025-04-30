package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import okhttp3.internal.wait

@Composable
fun RoomSearchContent(
    modifier: Modifier = Modifier,
    address: String,
    participantCount: Int,
    isMapVisible: Boolean = false,
    onMapToggleClick: (Boolean) -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_direction),
                contentDescription = "User Direction",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.size(42.dp))

            Row(
                modifier = Modifier
                    .padding(bottom = 21.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .padding(horizontal = 6.5.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map_toggle),
                    contentDescription = "Map Toggle",
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable { onMapToggleClick(isMapVisible) }
                )
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .background(
                            if (isMapVisible) Color(0xFFD8FCF0)
                            else Color.White
                        )
                        .padding(start = 20.dp, end = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_room_participant_40),
                        contentDescription = null,
                        tint = Color(0x8000E397)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "참가 인원 ${participantCount}명",
                        style = typography.medium.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF606060)
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFFD9D9D9))
                        .size(64.dp)
                        .clickable { onDeleteClick() }
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_room_delete_36),
                        contentDescription = "Map Toggle",
                        tint = Color.Unspecified,
                    )
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
        participantCount = 6
    )
}