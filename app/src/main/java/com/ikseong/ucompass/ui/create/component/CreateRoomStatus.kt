package com.ikseong.ucompass.ui.create.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun CreateRoomStatus(
    roomTitle: String,
    copyLink : String
) {
    Column {
        CreateRoomStatus(
            iconRes = R.drawable.ic_create_finish_title,
            status = "방제목",
            value = roomTitle
        )
        Spacer(modifier = Modifier.height(20.dp))
        CreateRoomStatus(
            iconRes = R.drawable.ic_create_finish_link,
            status = "방주소",
            value = copyLink
        )
    }
}

@Composable
private fun CreateRoomStatus(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    status: String,
    value: String
) {
    val statusTint = Color(0xFF606060)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = "Create Room Status Icon",
                tint = statusTint
            )
            Text(
                text = status,
                modifier = Modifier.padding(start = 8.dp),
                style = typography.medium.copy(fontSize = 18.sp, color = statusTint)
            )
        }
        Text(
            text = value,
            modifier = Modifier.padding(start = 8.dp),
            style = typography.regular.copy(fontSize = 18.sp)
        )
    }
}

@Preview
@Composable
private fun CreateRoomStatusPreview() {
    CreateRoomStatus(
        roomTitle = "과제 같이하실분~",
        copyLink = "DH274"
    )
}