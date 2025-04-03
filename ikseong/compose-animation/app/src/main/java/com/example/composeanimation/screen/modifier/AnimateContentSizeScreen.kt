package com.example.composeanimation.screen.modifier

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
fun AnimateContentSizeScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var expanded by remember { mutableStateOf(false) }
        var initialValue by remember { mutableStateOf("") }
        var targetValue by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            RandomBox(
                modifier = Modifier
                    .animateContentSize(
                        finishedListener = { initialVal, targetVal ->
                            initialValue = initialVal.toString()
                            targetValue = targetVal.toString()
                        }
                    )
                    .height(
                        if (expanded) 200.dp else 100.dp
                    )
            )
            Text(
                text = "initialValue : $initialValue",
                fontSize = 20.sp
            )
            Text(
                text = "targetValue : $targetValue",
                fontSize = 20.sp
            )

        }


        ButtonComponents(
            modifier = Modifier.align(Alignment.BottomCenter),
            event = {
                expanded = !expanded
            },
            navigateBack = navigateBack
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
private fun AnimateContentSizeScreenPreview() {
    AnimateContentSizeScreen(
        innerPadding = PaddingValues(16.dp), navigateBack = {}
    )
}

