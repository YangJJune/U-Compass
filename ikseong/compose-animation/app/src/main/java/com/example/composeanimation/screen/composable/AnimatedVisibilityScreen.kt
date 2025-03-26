package com.example.composeanimation.screen.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeanimation.component.ButtonComponents
import com.example.composeanimation.component.RandomBox


@Composable
fun AnimatedVisibilityScreen(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    navigateBack: () -> Unit,
    isPortrait: Boolean = true,
    enter: EnterTransition = fadeIn() + expandVertically(),
    exit: ExitTransition = fadeOut() + shrinkVertically(),
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
                AnimatedVisibility(
                    visible = visible,
                    enter = enter,
                    exit = exit,
                ) {
                    RandomBox(
                        modifier = Modifier.animateEnterExit(
                            enter = scaleIn()
                        )
                    )
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
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RandomBox()
                AnimatedVisibility(
                    visible = visible,
                    enter = enter,
                    exit = exit,
                ) {
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

@Preview(showBackground = true)
@Composable
private fun AnimatedVisibilityFadeInPreview() {
    val density = LocalDensity.current

    AnimatedVisibilityScreen(
        navigateBack = {},
        isPortrait = true,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkOut() + fadeOut()
    )
}

