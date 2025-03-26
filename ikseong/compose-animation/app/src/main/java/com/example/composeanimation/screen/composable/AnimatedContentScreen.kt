package com.example.composeanimation.screen.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeanimation.component.ButtonComponents

@Composable
fun AnimatedContentScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit
) {
    var count by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AnimatedContent(
            modifier = Modifier.align(Alignment.Center),
            targetState = count,
            label = "animated content",
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) { targetCount ->
            Text(
                text = "Count: $targetCount",
                fontSize = MaterialTheme.typography.displayLarge.fontSize
            )
        }

        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = {
                count++
            },
            navigateBack = navigateBack
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun AnimatedContentScreenPreview() {
    AnimatedContentScreen(
        innerPadding = PaddingValues(),
        navigateBack = {}
    )
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun AnimatedContentPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier.align(Alignment.Center),
        ) {
            var count by remember { mutableIntStateOf(0) }
            Button(onClick = { count++ }) {
                Text("Add")
            }
            AnimatedContent(
                targetState = count,
                label = "animated content"
            ) { targetCount ->
                // Make sure to use `targetCount`, not `count`.
                Text(text = "Count: $targetCount")
            }
        }
    }
}

