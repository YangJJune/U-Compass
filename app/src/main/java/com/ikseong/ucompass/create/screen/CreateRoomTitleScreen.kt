package com.ikseong.ucompass.create.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.common.component.UCompassButton
import com.ikseong.ucompass.common.component.UCompassLogo
import com.ikseong.ucompass.common.component.UCompassTextField
import com.ikseong.ucompass.create.component.CreateRoomContent

@Composable
fun CreateRoomTitleScreen(
    padding: PaddingValues
) {
    var title by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {

        Column {
            UCompassLogo()
            Spacer(modifier = Modifier.height(120.dp))
            CreateRoomContent()
            UCompassTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .padding(horizontal = 25.dp),
                text = title,
                placeholder = "방 제목을 입력하세요.",
                verticalPadding = 15.dp,
                onValueChange = { title = it }
            )
        }

        UCompassButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 21.dp)
                .height(62.dp)
                .align(Alignment.BottomCenter),
            text = "생성하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397)
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateRoomTitleScreenPreview() {
    CreateRoomTitleScreen(
        padding = PaddingValues(0.dp)
    )
}