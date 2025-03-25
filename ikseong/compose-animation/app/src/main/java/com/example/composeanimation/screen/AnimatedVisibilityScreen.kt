package com.example.composeanimation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeanimation.component.ButtonComponents
import com.example.composeanimation.component.RandomBox


@Composable
fun AnimatedVisibilityScreen(
    navigateBack: () -> Unit,
) {
    var visible by remember {
        mutableStateOf(true)
    }


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {

            AnimatedVisibility(visible) {
                RandomBox()
            }
            RandomBox()
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

@Preview
@Composable
private fun AnimatedVisibilityPreview() {
    AnimatedVisibilityScreen(
        navigateBack = {}
    )
}