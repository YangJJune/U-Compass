package com.example.composelayout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class BoxItem(
    val width: Dp,
    val height: Dp,
    val color: Color
)

val boxItems = List(15) {
    BoxItem(
        width = Random.nextInt(50, 200).dp,
        height = Random.nextInt(50, 200).dp,
        color = Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    )
}

@Composable
fun BoxItemComposable(
    boxItem: BoxItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(boxItem.width, boxItem.height)
            .background(boxItem.color),
        contentAlignment = Alignment.Center
    ) {
        Text("Box", color = Color.White)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoxItemFlowRow(
    horizontalArrangement: Arrangement.Horizontal,
    verticalArrangement: Arrangement.Vertical,
    alignment: Alignment.Vertical,
    maxItemsInEachRow: Int = Int.MAX_VALUE
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        maxItemsInEachRow = maxItemsInEachRow
    ) {
        boxItems.forEach { boxItem ->
            BoxItemComposable(
                modifier = Modifier.align(alignment),
                boxItem = boxItem,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowStartTop() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Top,
        alignment = Alignment.Top
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceBetweenBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Bottom,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowEndCenter() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.End,
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.CenterVertically
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceAroundTop() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.Top,
        alignment = Alignment.Top
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpacedByCenter() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.CenterVertically
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowStartBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Bottom,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceBetweenTop() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Top,
        alignment = Alignment.Top
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowEndBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.End,
        verticalArrangement = Arrangement.Bottom,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceAroundCenter() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.CenterVertically
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpacedByTop() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Top,
        alignment = Alignment.Top
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpacedByBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Bottom,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpacedByCenterTop() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.Top
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpacedByCenterBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceAroundBottom() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.Bottom,
        alignment = Alignment.Bottom
    )
}

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun FlowRowSpaceBetweenCenter() {
    BoxItemFlowRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.Center,
        alignment = Alignment.CenterVertically
    )
}