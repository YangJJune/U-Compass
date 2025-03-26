package com.example.composeimage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeimage.R

@Composable
fun ImageSliderScreen(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit
) {
    val radius = remember { mutableStateOf(Dp(0f)) } // 0 ~ 100
    val borderStroke = remember { mutableStateOf(Dp(0f)) }  // 0 ~ 10
    val aspectRatio = remember { mutableFloatStateOf(1f) } // 0 ~ 100
    val saturation = remember { mutableFloatStateOf(1f) } // 0 ~ 1
    val contrast = remember { mutableFloatStateOf(2f) } // 0f..10f (1 should be default)
    val brightness = remember { mutableFloatStateOf(-180f) } // -255f..255f (0 should be default)
    val blur = remember { mutableFloatStateOf(0f) } // 0f..25f (0 should be default)
    val colorMatrix = remember {
        mutableStateOf(
            floatArrayOf(
                contrast.floatValue, 0f, 0f, 0f, brightness.floatValue,
                0f, contrast.floatValue, 0f, 0f, brightness.floatValue,
                0f, 0f, contrast.floatValue, 0f, brightness.floatValue,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            modifier = Modifier
                .size(240.dp)
                .aspectRatio(aspectRatio.floatValue)
                .blur(
                    radius = Dp(blur.floatValue),
                    edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(radius.value))
                )
                .clip(RoundedCornerShape(radius.value))
                .border(borderStroke.value, Color.Black, RoundedCornerShape(radius.value)),
            painter = painterResource(id = R.drawable.img_dog),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix(
                floatArrayOf(
                    contrast.floatValue, 0f, 0f, 0f, brightness.floatValue,
                    0f, contrast.floatValue, 0f, 0f, brightness.floatValue,
                    0f, 0f, contrast.floatValue, 0f, brightness.floatValue,
                    0f, 0f, 0f, 1f, 0f
                )
            ).apply {
                setToSaturation(saturation.floatValue)
            })
        )
        Spacer(modifier = Modifier.size(16.dp))
        RangeSlider(
            text = "Radius: ${radius.value}",
            value = radius.value.value,
            valueRange = 0f..100f,
            onValueChange = { value ->
                radius.value = Dp(value)
            }
        )
        RangeSlider(
            text = "Border Stroke: ${borderStroke.value}",
            value = borderStroke.value.value,
            valueRange = 0f..10f,
            onValueChange = { value ->
                borderStroke.value = Dp(value)
            }
        )
        RangeSlider(
            text = "Aspect Ratio: ${aspectRatio.floatValue}",
            value = aspectRatio.floatValue,
            valueRange = 0f..100f,
            onValueChange = { value ->
                aspectRatio.floatValue = value
            }
        )
        RangeSlider(
            text = "Saturation: ${saturation.floatValue}",
            value = saturation.floatValue,
            valueRange = 0f..1f,
            onValueChange = { value ->
                saturation.floatValue = value
            }
        )
        RangeSlider(
            text = "Contrast: ${contrast.floatValue}",
            value = contrast.floatValue,
            valueRange = 0f..10f,
            onValueChange = { value ->
                contrast.floatValue = value
            }
        )
        RangeSlider(
            text = "Brightness: ${brightness.floatValue}",
            value = brightness.floatValue,
            valueRange = -255f..255f,
            onValueChange = { value ->
                brightness.floatValue = value
            }
        )
        RangeSlider(
            text = "Blur: ${blur.floatValue}",
            value = blur.floatValue,
            valueRange = 0f..25f,
            onValueChange = { value ->
                blur.floatValue = value
            }
        )
    }
}

@Composable
fun RangeSlider(
    modifier: Modifier = Modifier,
    text: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 32.sp
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange
        )
    }
}


@Preview(showBackground = true, heightDp = 1600)
@Composable
private fun ImageSliderScreenPreview() {
    ImageSliderScreen(
        innerPadding = PaddingValues(16.dp),
        navigateBack = {}
    )
}

@Preview
@Composable
private fun SliderPreview() {
    val text = remember { mutableFloatStateOf(0f) }
    Column {
        Text(
            text = "Slider ${text.floatValue}",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 32.sp
        )
        Slider(
            value = text.floatValue,
            onValueChange = { value ->
                text.floatValue = value
            }
        )
    }
}