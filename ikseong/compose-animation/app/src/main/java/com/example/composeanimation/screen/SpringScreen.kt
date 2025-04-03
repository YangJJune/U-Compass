package com.example.composeanimation.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeanimation.component.ButtonComponents
import kotlin.math.roundToInt

@Composable
fun SpringScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit,
) {
    var moved by remember { mutableStateOf(false) }
    val pxToMove = with(LocalDensity.current) {
        250.dp.toPx().roundToInt()
    }
    val offset1 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offset2 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offset3 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offset4 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offset5 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    val offset6 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val offset7 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )
    val offset8 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    val offset9 by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        },
        label = "offset",
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset { offset1 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "DampingRatioNoBouncy")
            Box(
                modifier = Modifier
                    .offset { offset2 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "DampingRatioLowBouncy")
            Box(
                modifier = Modifier
                    .offset { offset3 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "DampingRatioMediumBouncy")
            Box(
                modifier = Modifier
                    .offset { offset4 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "DampingRatioHighBouncy")
            Spacer(Modifier.size(50.dp))

            Box(
                modifier = Modifier
                    .offset { offset5 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "StiffnessVeryLow")
            Box(
                modifier = Modifier
                    .offset { offset6 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "StiffnessLow")

            Box(
                modifier = Modifier
                    .offset { offset7 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "StiffnessMediumLow")
            Box(
                modifier = Modifier
                    .offset { offset8 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "StiffnessMedium")
            Box(
                modifier = Modifier
                    .offset { offset9 }
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = androidx.compose.ui.graphics.Color.Red)
            )
            Text(text = "StiffnessHigh")



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

@Preview(showBackground = true)
@Composable
private fun SpringScreenPreview() {
    SpringScreen(innerPadding = PaddingValues(16.dp), navigateBack = {})
}