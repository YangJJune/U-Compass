package com.example.composeanimation.screen.modifier

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeanimation.component.ButtonComponents
import com.example.composeanimation.component.RandomBox


@Composable
fun AnimatePaddingScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit
) {
    var toggled by remember { mutableStateOf(false)    }
    var paddingValue by remember {  mutableStateOf("") }
    val animatedPadding by animateDpAsState(
        if (toggled) {
            0.dp
        } else {
            20.dp
        },
        label = "padding",
        finishedListener = {
            paddingValue = it.toString()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            RandomBox(
                modifier = Modifier
                    .padding(animatedPadding)
            )
            Text(
                text = "paddingValue : $paddingValue",
                fontSize = 20.sp
            )

        }


        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = {
                toggled = !toggled
            },
            navigateBack = navigateBack
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun AnimatePaddingScreenPreview() {
    AnimatePaddingScreen(
        innerPadding = PaddingValues(16.dp), navigateBack = {}
    )
}

