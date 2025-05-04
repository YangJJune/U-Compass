package com.ikseong.ucompass.ui.util.viewutil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    object : PaddingValues {
        override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
            this@plus.calculateLeftPadding(layoutDirection) + other.calculateLeftPadding(
                layoutDirection
            )

        override fun calculateTopPadding(): Dp =
            this@plus.calculateTopPadding() + other.calculateTopPadding()

        override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
            this@plus.calculateRightPadding(layoutDirection) + other.calculateRightPadding(
                layoutDirection
            )

        override fun calculateBottomPadding(): Dp =
            this@plus.calculateBottomPadding() + other.calculateBottomPadding()
    }