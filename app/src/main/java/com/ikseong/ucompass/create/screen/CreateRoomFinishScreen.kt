package com.ikseong.ucompass.create.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ikseong.ucompass.common.component.UCompassButton
import com.ikseong.ucompass.common.component.UCompassLogo
import com.ikseong.ucompass.create.component.CreateRoomFinishContent
import com.ikseong.ucompass.create.component.CreateRoomStatus
import com.ikseong.ucompass.create.viewmodel.CreateViewModel

@Composable
fun CreateRoomFinishRoute(
    padding: PaddingValues,
    navigateToHome: () -> Unit = {},
    shareRoomLink: () -> Unit = {},
    viewModel: CreateViewModel = hiltViewModel()
) {
    CreateRoomFinishScreen(
        padding = padding,
        navigateToHome = navigateToHome,
        shareRoomLink = shareRoomLink
    )
}

@Composable
fun CreateRoomFinishScreen(
    padding: PaddingValues,
    navigateToHome: () -> Unit = {},
    shareRoomLink: () -> Unit = {},
) {
    val roomTitle by remember { mutableStateOf("") }
    val copyLink by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
        ) {
            UCompassLogo()
            Spacer(modifier = Modifier.height(101.dp))
            CreateRoomFinishContent()
            CreateRoomStatus(
                roomTitle = roomTitle,
                copyLink = copyLink
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 21.dp)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UCompassButton(
                modifier = Modifier
                    .weight(114f)
                    .height(62.dp),
                text = "공유",
                color = Color(0xFFD9D9D9),
                fontSize = 20.sp
            ) { shareRoomLink() }
            Spacer(modifier = Modifier.weight(12f))
            UCompassButton(
                modifier = Modifier
                    .weight(242f)
                    .height(62.dp),
                text = "확인",
                color = Color(0xFF00E397),
                fontSize = 20.sp
            ) { navigateToHome() }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateRoomFinishScreenPreview() {
    CreateRoomFinishScreen(
        padding = PaddingValues()
    )
}