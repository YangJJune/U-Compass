package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomDefaultContent(
    modifier: Modifier = Modifier,
    address: String,
    participantCount: Int,
    onSearchClick: (Boolean) -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = LottieConstants.IterateForever
    )
    RoomGuideComponent(
        modifier = Modifier
            .padding(top = 64.dp),
        address = address,
    )
    Box(
        modifier = Modifier.padding(top = 48.dp)
            .clickable { onSearchClick(true) }
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(323.dp),
            composition = composition,
            progress = { progress },
        )
        Icon(
            painter = painterResource(R.drawable.ic_lottie_arrow),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center),
            tint = Color.Unspecified
        )
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .width(194.dp)
                .height(72.dp)
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(25.dp),
                    color = Color(0xFF00E397)
                )
                .clickable { },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_room_participant_40),
                contentDescription = null,
                tint = Color(0x8000E397)
            )
            Text(
                text = "참가 인원 ${participantCount}명",
                style = typography.medium.copy(
                    fontSize = 18.sp,
                    color = Color(0xFF606060)
                )
            )
        }
    }


}

@Preview
@Composable
private fun RoomDefaultContentPreview() {
    RoomDefaultContent(address = "서울특별시 장안동", participantCount = 6)
}