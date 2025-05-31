package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    name: String,
    direction: Direction,
    distance: Int,
    type: DistanceType,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = name,
            style = typography.medium.copy(
                fontSize = 14.sp,
                color = Color.White
            )
        )
        Text(
            text = "${direction.name} ${distance}M",
            style = typography.medium.copy(
                fontSize = 14.sp,
                color = Color.White
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
        direction = Direction.N,
        distance = 500,
        type = DistanceType.ZERO,
    )
}