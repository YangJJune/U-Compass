package com.ikseong.ucompass.common.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.ui.theme.UCompassTheme
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun UCompassButton(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit,
    color: Color,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = contentPadding
    ) {
        Text(
            text = text,
            color = Color.White,
            style = typography.semiBold.copy(
                fontSize = fontSize
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UCompassButtonPreview() {
    UCompassTheme {
        UCompassButton(
            modifier = Modifier
                .padding(10.dp)
                .width(368.dp)
                .height(62.dp),
            text = "생성하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397),
            onClick = {}
        )
    }
}