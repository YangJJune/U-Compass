package com.example.composeanimation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeanimation.component.ButtonComponents
import com.example.composeanimation.component.RandomBox


@Composable
fun AnimatedVisibilityScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    navigateBack: () -> Unit,
    isPortrait: Boolean = true
) {
    var visible by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        if (isPortrait) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                RandomBox()
                AnimatedVisibility(visible) {
                    RandomBox()
                }
                RandomBox()
                if (visible) {
                    RandomBox()
                }

            }

            ButtonComponents(
                modifier = Modifier.align(Alignment.BottomCenter),
                event = {
                    visible = !visible
                },
                navigateBack = navigateBack
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RandomBox()
                AnimatedVisibility(visible) {
                    RandomBox()
                }
                RandomBox()
                if (visible) {
                    RandomBox()
                }
            }
            ButtonComponents(
                modifier = Modifier.align(Alignment.BottomCenter),
                event = {
                    visible = !visible
                },
                navigateBack = navigateBack
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun AnimatedVisibilityVerticalPreview() {
    AnimatedVisibilityScreen(
        navigateBack = {},
        isPortrait = true
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400)
@Composable
private fun AnimatedVisibilityHorizontalPreview() {
    AnimatedVisibilityScreen(
        navigateBack = {},
        isPortrait = false
    )
}