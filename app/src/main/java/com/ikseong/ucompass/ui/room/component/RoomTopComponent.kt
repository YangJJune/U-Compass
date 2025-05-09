package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomTopComponent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isSearchMode: Boolean,
    isMapVisible: Boolean,
    roomName: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left_fill_36),
                contentDescription = "Back Icon",
                tint = if (isMapVisible) Color(0xFF606060) else Color.White
            )
        }
        Text(
            text = roomName,
            style = typography.semiBold.copy(
                fontSize = 24.sp,
                color = if (isMapVisible) Color.Black else Color.White
            )
        )

        IconButton(
            onClick = onDeleteClick,
            enabled = !isSearchMode
        ) {
            if (!isSearchMode) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_room_delete_36),
                    contentDescription = "Delete Room Icon",
                    tint = Color(0xFF606060)
                )
            }
        }
    }
}

@Preview
@Composable
private fun RoomTopComponentPreview() {
    RoomTopComponent(
        onBackClick = {},
        onDeleteClick = {},
        roomName = "위치 찾기 방 1",
        isSearchMode = true,
        isMapVisible = false
    )
}
