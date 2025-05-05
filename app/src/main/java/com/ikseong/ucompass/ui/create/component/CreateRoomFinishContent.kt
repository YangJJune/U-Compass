package com.ikseong.ucompass.ui.create.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun CreateRoomFinishContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_create_finish_content),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "석준님\n" +
                    "방생성을 완료했어요!",
            style = typography.semiBold.copy(
                fontSize = 32.sp
            ),
            color = Color.Black,
            textAlign = TextAlign.Center
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
private fun CreateRoomFinishContentPreview() {
    CreateRoomFinishContent()
}