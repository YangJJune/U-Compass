package com.ikseong.ucompass.common.component

import androidx.compose.foundation.border
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
    val containerColor = Color(0xFFF6F6F6)
    val borderColor = Color(0xFFE8E8E8)
    val textColor = Color.Black.copy(alpha = 0.5f)

    TextField(
        modifier = modifier
            .border(
                color = borderColor,
                width = 1.dp,
                shape = RoundedCornerShape(100.dp)
            )
            .padding(
                vertical = verticalPadding.minus(12.dp)
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
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun UCompassTextFieldPreview() {
    UCompassTextField(
        text = "양석준",
        onValueChange = {},
        verticalPadding = 15.dp,
    )
}