package com.example.composeanimation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
    innerPadding: PaddingValues,
    navigateToAnimatedVisibility: () -> Unit = {},
    navigateToAnimatedContent: () -> Unit = {},
    navigateToCrossfade: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(onClick = navigateToAnimatedVisibility) {
            Text(text = "Animated Visibility")
        }
        Button(onClick = navigateToAnimatedContent) {
            Text(text = "Animated Content")
        }
        Button(onClick = navigateToCrossfade) {
            Text(text = "Crossfade")
        }

    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        innerPadding = PaddingValues(0.dp)
    )
}