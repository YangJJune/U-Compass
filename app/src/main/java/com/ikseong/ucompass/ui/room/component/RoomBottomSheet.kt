package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.ParticipantInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomBottomSheetDragHandle(
    modifier: Modifier = Modifier,
) {
    BottomSheetDefaults.DragHandle(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 140.dp),
        width = 66.dp,
        height = 4.dp,
        color = Color(0xFFE8E8E8)
    )
}

@Composable
fun RoomBottomSheet(
    modifier: Modifier = Modifier,
    participantInfo: List<ParticipantInfo>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 21.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 11.dp),
            text = "전체 선택",
            style = typography.medium.copy(
                fontSize = 14.sp,
                color = Color(0xFF606060)
            ),
            textDecoration = TextDecoration.Underline
        )
        LazyColumn(
            contentPadding = PaddingValues(vertical = 23.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(participantInfo) { participant ->
                ParticipantItem(
                    participant = participant
                )
            }
        }
    }
}

@Composable
fun ParticipantItem(
    modifier: Modifier = Modifier,
    participant: ParticipantInfo
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(77.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x80CCF9EA))
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(37.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD9D9D9))
            ) {
                // TODO: 나중에 프로필 이미지로 바꾸기
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    painter = painterResource(R.drawable.ic_user_info_main),
                    contentDescription = "User Info",
                    tint = Color.Unspecified
                )
            }
            Text(
                text = participant.name,
                style = typography.medium.copy(
                    fontSize = 18.sp,
                    color = Color.Black
                )
            )
        }

        Icon(
            painter = painterResource(
                if (participant.isShown) R.drawable.ic_participant_shown
                else R.drawable.ic_participant_unshown
            ),
            contentDescription = "shown",
            tint = Color.Unspecified
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RoomBottomSheetPreview() {
    RoomBottomSheet(
        participantInfo = listOf(
            ParticipantInfo(
                "name",
                "profileUrl",
                Direction.N,
                100,
                true
            ),
            ParticipantInfo(
                "name",
                "profileUrl",
                Direction.N,
                100,
                false
            ),
            ParticipantInfo(
                "name",
                "profileUrl",
                Direction.N,
                100,
                true
            )
        )
    )
}