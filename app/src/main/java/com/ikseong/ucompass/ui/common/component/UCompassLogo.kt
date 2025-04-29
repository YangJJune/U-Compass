package com.ikseong.ucompass.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ikseong.ucompass.R

@Composable
fun UCompassLogo(
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp)
            .padding(top = 32.dp),
        painter = painterResource(R.drawable.img_ucompass_logo),
        contentDescription = "UCompass Logo",
        alignment = Alignment.BottomStart
    )
}


@Preview
@Composable
private fun UCompassLogoPreview() {
    UCompassLogo()
}