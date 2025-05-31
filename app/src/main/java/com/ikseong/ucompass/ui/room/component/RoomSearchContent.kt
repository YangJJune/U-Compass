package com.ikseong.ucompass.ui.room.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun RoomSearchContent(
    modifier: Modifier = Modifier,
    address: String,
    isMapVisible: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .height(46.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(
                    color = if (isMapVisible) Color.White else Color.White.copy(alpha = 0.8f)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_main),
                contentDescription = null,
                tint = Color(0xFF606060)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = address,
                style = typography.medium.copy(
                    fontSize = 16.sp,
                    color = Color(0xFF606060)
                )
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.ic_user_direction),
                contentDescription = "User Direction",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.size(42.dp))

        }
    }
}

@Preview
@Composable
private fun RoomSearchContentPreview() {
    RoomSearchContent(
        address = "123 Main St, City, Country",
    )
}