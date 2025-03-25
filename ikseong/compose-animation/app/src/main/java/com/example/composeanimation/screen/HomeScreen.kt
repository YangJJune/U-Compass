package com.example.composeanimation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navigateToAnimatedVisibility: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(innerPadding),
    ) {
        Button(onClick = navigateToAnimatedVisibility) {
            Text(text = "Animated Visibility")
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