package com.ikseong.ucompass.common.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun UCompassTextField(
    modifier: Modifier = Modifier,
    verticalPadding: Dp,
    text: String,
    style: TextStyle = typography.medium,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
) {
    val borderColor = Color(0xFFE8E8E8)
    val textColor = Color.Black.copy(alpha = 0.5f)

    // TODO : BasicTextField 로 변경
    TextField(
        modifier = modifier
            .border(
                color = borderColor,
                width = 1.dp,
                shape = RoundedCornerShape(100.dp)
            )
            .padding(
                vertical = if (verticalPadding > 12.dp) verticalPadding.minus(12.dp) else 0.dp
            ),
        value = text,
        onValueChange = onValueChange,
        textStyle = style,
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = style
                )
            }
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(100.dp),
        interactionSource = MutableInteractionSource(),
    )
}

@Preview(showBackground = true)
@Composable
private fun UCompassTextFieldPreview() {
    UCompassTextField(
        modifier = Modifier.padding(20.dp),
        text = "양석준",
        onValueChange = {},
        verticalPadding = 15.dp,
    )
}