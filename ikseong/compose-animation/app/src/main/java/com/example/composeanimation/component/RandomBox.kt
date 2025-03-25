package com.example.composeanimation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RandomBox(modifier: Modifier = Modifier) {
    val color = remember { mutableStateOf(colorList.random()) }
    Box(
        modifier = modifier
            .width(200.dp)
            .height(160.dp)
            .background(color.value)
    )
}

val colorList = listOf(
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan,
    Color.Gray,
    Color.Black,
    Color.White
)

