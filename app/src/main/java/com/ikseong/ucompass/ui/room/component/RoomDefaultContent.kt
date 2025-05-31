package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ikseong.ucompass.R

@Composable
fun ColumnScope.RoomDefaultContent(
    modifier: Modifier = Modifier,
    address: String,
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
        modifier = Modifier
            .padding(top = 48.dp)
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

}

@Preview
@Composable
private fun RoomDefaultContentPreview() {
    Column { RoomDefaultContent(address = "서울특별시 장안동",) }
}