package com.ikseong.ucompass.ui.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun LocationPermissionDialog(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = { },
    onDismissRequest: () -> Unit = { },
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = modifier
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "워치 권한 허용",
                    style = typography.semiBold.copy(
                        fontSize = 24.sp,
                    )
                )
                Icon(
                    modifier = Modifier.padding(top = 32.dp),
                    painter = painterResource(R.drawable.ic_location_permission_dialog),
                    contentDescription = "위치 권한 팝업 아이콘",
                    tint = Color.Unspecified,
                )
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp),
                    text = "서비스 이용을 위해 위치 권한이 필요합니다.",
                    style = typography.regular.copy(
                        fontSize = 13.sp,
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    Icon(
                        modifier = Modifier.padding(top = 1.dp),
                        painter = painterResource(R.drawable.ic_location_permission_dialog_2),
                        contentDescription = "위치 권한 팝업 아이콘",
                        tint = Color.Unspecified,
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "현재 위치 기반 서비스",
                            style = typography.medium.copy(
                                fontSize = 18.sp,
                            )
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "주변 정보를 쉽게 찾아보실 수 있습니다",
                            style = typography.medium.copy(
                                fontSize = 12.sp,
                                color = Color(0xFF606060),
                            )
                        )
                    }
                }
                UCompassButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = "위치 권한 허용하기",
                    fontSize = 16.sp,
                    color = Color(0xFF00E397),
                    contentPadding = PaddingValues(vertical = 15.5.dp)
                ) { onRequestPermission() }

                UCompassButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    text = "거부 하기",
                    fontSize = 16.sp,
                    color = Color(0xFFD9D9D9),
                    contentPadding = PaddingValues(vertical = 15.5.dp)
                ) { onDismissRequest() }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    text = "거부시 서비스 이용에 지장이 있을 수 있습니다.",
                    textAlign = TextAlign.Center,
                    style = typography.regular.copy(
                        fontSize = 12.sp,
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationPermissionDialogPreview() {
    LocationPermissionDialog()
}