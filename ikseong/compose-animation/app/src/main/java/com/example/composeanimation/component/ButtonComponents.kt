package com.example.composeanimation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ButtonComponents(
    modifier: Modifier = Modifier,
    event : () -> Unit,
    navigateBack: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = event
        ) {
            Text(
                text = "Event"
            )
        }
        Button(
            onClick = navigateBack
        ) {
            Text(
                text = "Go Back"
            )
        }
    }
}