package com.ikseong.ucompass.ui.common.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import com.ikseong.ucompass.ui.util.viewutil.noRippleClickable


@Composable
fun AddressInfo(
    modifier: Modifier = Modifier,
    address: String = "",
    onAddressClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .noRippleClickable { onAddressClick() }
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            painter = painterResource(R.drawable.ic_location_main),
            contentDescription = "위치",
            tint = Color(0xFF606060)
        )

        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            text = address,
            style = typography.medium.copy(
                fontSize = 20.sp,
                color = Color(0x80000000)
            )
        )
    }
}