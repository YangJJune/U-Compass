package com.example.composesensor.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    navigateToSensor: () -> Unit,
    navigateToLocation: () -> Unit,
    navigateToNaverMap: () -> Unit,
    navigateToFingerprinting: () -> Unit,
    navigateToTriangulation: () -> Unit,
    navigateToInsNavigation: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Button(
            onClick = navigateToSensor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "센서 정보")
        }

        Button(
            onClick = navigateToLocation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "위치 정보")
        }

        Button(
            onClick = navigateToNaverMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "네이버 지도")
        }

        Button(
            enabled = false,
            onClick = navigateToFingerprinting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "핑거프린팅")
        }

        Button(
            onClick = navigateToTriangulation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "삼각측량")
        }
        
        Button(
            onClick = navigateToInsNavigation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "INS 내비게이션")
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
    HomeScreen(PaddingValues(0.dp), {}, {}, {}, {}, {}, {})
}