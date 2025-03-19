package com.example.composelayout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ComposeModifier(modifier: Modifier = Modifier) {
    Modifier.padding()
    Modifier.navigationBarsPadding()

    Modifier.width(0.dp)
    Modifier.height(0.dp)
    Modifier.size(0.dp)

    Modifier.aspectRatio(1f)
    Modifier.offset(x = 0.dp, y = 0.dp)
    Modifier.shadow(elevation = 0.dp)

    Modifier.then(Modifier.padding())
    Modifier.requiredWidth(0.dp)


}

fun Modifier.myBackground(color: Color) = this then padding(16.dp)
    .clip(RoundedCornerShape(8.dp))
    .background(color)

@Composable
fun Modifier.fade(enable: Boolean): Modifier {
    val alpha by animateFloatAsState(if (enable) 0.5f else 1.0f)
    return this then Modifier.graphicsLayer { this.alpha = alpha }
}

@Composable
fun Modifier.fadedBackground(): Modifier {
    val color = LocalContentColor.current
    return this then Modifier.background(color.copy(alpha = 0.5f))
}

@Preview(showBackground = true)
@Composable
private fun FadeModifierPreview() {
    var enable by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .size(100.dp)
            .fade(enable = enable)
    ) {
        Button(onClick = { enable = !enable }) {
            Text("Click me")
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun FadeBackgroundModifierPreview() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .fadedBackground()
    )
}
