package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.AddressInfo
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomGuideComponent(
    modifier: Modifier = Modifier,
    address: String,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "버튼을 눌러",
            style = typography.semiBold.copy(
                fontSize = 32.sp,
                lineHeight = 44.sp
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.img_ucompass_logo),
                contentDescription = "UCompass Logo",
                alignment = Alignment.BottomStart
            )
            Text(
                text = "를 시작하세요!",
                style = typography.semiBold.copy(
                    fontSize = 32.sp,
                    lineHeight = 44.sp
                )
            )
        }

        AddressInfo(
            modifier = Modifier.padding(top = 16.dp),
            address = address
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun RoomGuideComponentPreview() {
    RoomGuideComponent(
        address = "서울특별시, 장안동"
    )
}