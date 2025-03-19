package com.example.composelayout

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MyRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = Modifier,
        content = content
    ) { measurables, constraints ->

        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            var x = 0
            placeables.forEach { placeable ->
                placeable.place(x, 0)
                x += placeable.width
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MyRowPreview() {
    MyRow{
        repeat(10) {
            Text("Item $it")
        }
    }
}