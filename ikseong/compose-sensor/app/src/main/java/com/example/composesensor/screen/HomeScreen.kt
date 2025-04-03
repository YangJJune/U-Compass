package com.example.composesensor.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navigateToSensor: () -> Unit = {},
    navigateToCamera: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        MyButton(
            text = "Sensor",
        ) {
            navigateToSensor()
        }
        MyButton(
            text = "Camera",
        ) {
            navigateToCamera()
        }
    }
}

@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = text)
    }

}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}