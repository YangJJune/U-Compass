package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomDeleteDialog(
    modifier: Modifier = Modifier,
    isHost: Boolean,
    onDismissRequest: () -> Unit,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = modifier
                .width(320.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isHost) "방 삭제하기" else "방 나가기",
                    style = typography.semiBold.copy(
                        fontSize = 24.sp,
                    )
                )
                Icon(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    painter = painterResource(id = R.drawable.ic_room_delete_warning),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Row(
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = if (isHost) "방을 " else "방에서 ",
                        style = typography.medium.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF606060)
                        )
                    )
                    Text(
                        text = if (isHost) "영구적으로 " else "즉시 ",
                        style = typography.bold.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF606060)
                        )
                    )
                    Text(
                        text = if (isHost) "삭제하시겠습니까?" else "나가시겠습니까?",
                        style = typography.medium.copy(
                            fontSize = 18.sp,
                            color = Color(0xFF606060)
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UCompassButton(
                        modifier = Modifier
                            .width(130.dp)
                            .height(51.dp),
                        text = "확인",
                        fontSize = 16.sp,
                        color = Color(0xFFD9D9D9)
                    ) { onDeleteClick() }


                    UCompassButton(
                        modifier = Modifier
                            .width(130.dp)
                            .height(51.dp),
                        text = "취소",
                        fontSize = 16.sp,
                        color = Color(0xFF00E397)
                    ) { onCancelClick() }
                }
            }
        }
    }

}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun RoomDeleteDialogPreview() {
    RoomDeleteDialog(
        isHost = true,
        onDismissRequest = {},
        onDeleteClick = {},
        onCancelClick = {}
    )
}