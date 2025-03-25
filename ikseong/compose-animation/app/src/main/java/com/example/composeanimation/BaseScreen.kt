package com.example.composeanimation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeanimation.component.ButtonComponents

@Composable
fun BaseScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {

        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = { },
            navigateBack = navigateBack
        )
    }
}

@Preview
@Composable
private fun BaseScreenPreview() {

}