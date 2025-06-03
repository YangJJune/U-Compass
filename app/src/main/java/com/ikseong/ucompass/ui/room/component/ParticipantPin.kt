package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.ui.model.Direction
import com.ikseong.ucompass.ui.model.DistanceType
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun ParticipantPin(
    modifier: Modifier = Modifier,
    isMapVisible: Boolean = false,
    name: String,
    direction: Direction,
    distance: Int,
    type: DistanceType,
) {
    val textColor = if (isMapVisible) Color.Black else Color.White

    Column(
        modifier = modifier
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            style = typography.medium.copy(
                fontSize = 14.sp,
                color = textColor
            )
        )
        Text(
            text = "${direction.name} ${distance}M",
            style = typography.medium.copy(
                fontSize = 14.sp,
                color = textColor
            )
        )
        Icon(
            modifier = Modifier
                .rotate(direction.rotation)
                .size(type.size),
            painter = painterResource(id = type.iconRes),
            contentDescription = "Participant Pin",
            tint = Color.Unspecified
        )
    }
}

@Preview(widthDp = 200, heightDp = 200)
@Composable
private fun ParticipantPinPreview() {
    ParticipantPin(
        name = "홍길동",
        direction = Direction.NE,
        distance = 500,
        type = DistanceType.ZERO,
    )
}