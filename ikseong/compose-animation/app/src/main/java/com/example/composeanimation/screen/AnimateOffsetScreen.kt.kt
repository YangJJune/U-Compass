package com.example.composeanimation.screen

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeanimation.component.ButtonComponents
import com.example.composeanimation.component.RandomBox
import kotlin.math.roundToInt


@Composable
fun AnimateOffsetScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit
) {
    var moved by remember { mutableStateOf(false) }
    val pxToMove = with(LocalDensity.current) {
        100.dp.toPx().roundToInt()
    }

    val offset by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, pxToMove)
        } else {
            IntOffset.Zero
        },
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RandomBox()

            RandomBox(
                modifier = Modifier
                    .offset {
                        offset
                    }
            )
            RandomBox()
        }


        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = {
                moved = !moved
            },
            navigateBack = navigateBack
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun AnimateOffsetScreenPreview() {
    AnimateOffsetScreen(
        innerPadding = PaddingValues(16.dp), navigateBack = {}
    )
}

@Preview
@Composable
private fun AnimateOffsetWithLayoutPreview() {
    var moved by remember { mutableStateOf(false) }
    val offsetTarget = if (moved) {
        IntOffset(150, 150)
    } else {
        IntOffset.Zero
    }
    val offset = animateIntOffsetAsState(
        targetValue = offsetTarget, label = "offset"
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
            RandomBox()

            RandomBox(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val offsetValue = if (isLookingAhead) offsetTarget else offset.value
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width + offsetValue.x, placeable.height + offsetValue.y) {
                            placeable.placeRelative(offsetValue)
                        }
                    }
            )
            RandomBox()
        }


        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = {
                moved = !moved
            },
            navigateBack = {}
        )
    }
}

