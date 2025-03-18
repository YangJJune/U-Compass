package com.example.composenavigation.bottomnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composenavigation.ui.theme.ComposeNavigationTheme

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    visible: Boolean,
    tabs: List<BottomTab>,
    currentTab: BottomTab?,
    onTabSelected: (BottomTab) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
                .drawBehind {
                    val borderThickness = 1.dp.toPx()

                    drawLine(
                        color = Color.DarkGray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = borderThickness
                    )
                },
        ) {
            tabs.forEach { tab ->
                BottomBarItem(
                    tab = tab,
                    selected = tab == currentTab,
                    onClick = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    modifier: Modifier = Modifier,
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val itemSelectColor = if (selected) Color.Black else Color.DarkGray
    val itemSelectIcon = if (selected) tab.selectedIcon else tab.unselectedIcon

    Column(
        modifier = modifier
            .padding(vertical = 10.dp)
            .fillMaxHeight()
            .align(Alignment.CenterVertically)
            .weight(1f)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(itemSelectIcon),
            contentDescription = tab.description,
            tint = Color.Unspecified
        )
        Text(
            text = tab.label,
            color = itemSelectColor
        )
    }
}

@Preview
@Composable
private fun MainBottomBarPreview() {
    ComposeNavigationTheme {
        BottomBar(
            visible = true,
            tabs = BottomTab.entries,
            currentTab = BottomTab.HOME,
            onTabSelected = { },
        )
    }
}