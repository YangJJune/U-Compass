package com.ikseong.ucompass.create.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
fun CreateRoomTitleContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_create_title_content),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(77.dp))

        Text(
            text = "제목을 입력해주세요",
            style = typography.semiBold.copy(
                fontSize = 32.sp
            ),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "친구들에게 설명해줄 내용을 입력하세요.",
            style = typography.medium.copy(
                fontSize = 18.sp
            ),
            color = Color(0xFF606060)
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 60.dp),
            thickness = 1.dp,
            color = Color(0xFFD9D9D9)
        )

    }
}

@Preview
@Composable
private fun CreateRoomTitleContentPreview() {
    CreateRoomTitleContent()
}