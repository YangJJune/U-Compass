package com.ikseong.ucompass.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.ikseong.ucompass.R

@Composable
fun ProfileImageWithPlaceholder(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String = "프로필 이미지",
    size: Dp = 77.dp,
    shape: Shape = RoundedCornerShape(25.dp),
    backgroundColor: Color = Color(0xFF00E397),
    placeholderColor: Color = Color(0xFFCCF9EA),
    showLoadingIndicator: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.matchParentSize(),
            model = imageUrl,
            contentDescription = contentDescription,
            loading = {
                if (showLoadingIndicator) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(placeholderColor),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF00E397),
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    DefaultProfilePlaceholder(
                        size = size,
                        backgroundColor = placeholderColor
                    )
                }
            },
            error = {
                DefaultProfilePlaceholder(
                    size = size,
                    backgroundColor = placeholderColor
                )
            }
        )
    }
}

@Composable
private fun DefaultProfilePlaceholder(
    size: Dp,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size * 0.5f),
            painter = painterResource(R.drawable.ic_user_info_main),
            contentDescription = "기본 프로필 이미지",
            tint = Color(0xFF606060)
        )
    }
} 