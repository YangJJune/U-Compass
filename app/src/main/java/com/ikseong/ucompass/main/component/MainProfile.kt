package com.ikseong.ucompass.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ikseong.ucompass.R

@Composable
fun MainProfile(
    modifier: Modifier = Modifier,
    imageUrl : String = "",
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(110.dp)
            .height(114.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(color = Color(0xFFCCF9EA))
        )

        Box(
            modifier = Modifier
                .padding(start = 15.dp, top = 20.dp)
                .size(77.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(color = Color(0xFF00E397))
        ) {
            AsyncImage(
                modifier = Modifier.matchParentSize(),
                model = imageUrl,
                contentDescription = "프로필 이미지"
            )
        }

        Box(
            modifier = Modifier
                .size(43.dp)
                .clip(CircleShape)
                .background(color = Color(0xFFCCF9EA))
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(R.drawable.ic_edit_profile_main),
                contentDescription = "프로필 편집 아이콘",
                tint = Color(0xFF606060)
            )
        }
    }
}

@Preview
@Composable
private fun MainProfilePreview() {
    MainProfile()
}