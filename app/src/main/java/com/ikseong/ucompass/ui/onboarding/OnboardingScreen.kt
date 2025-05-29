package com.ikseong.ucompass.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography

@Composable
fun OnboardingScreen(
    padding: PaddingValues,
    navigateToOnboardingInput: () -> Unit = { }
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(118f))
        Column {
            Row {
                Image(
                    painter = painterResource(R.drawable.img_ucompass_logo),
                    contentDescription = "UCompass Logo",
                    alignment = Alignment.BottomStart
                )
                Text(
                    text = "와 함께",
                    style = typography.semiBold.copy(
                        fontSize = 32.sp
                    )
                )
            }
            Text(
                text = "친구들을 찾아보세요!",
                style = typography.semiBold.copy(
                    fontSize = 32.sp
                )
            )
        }
        Spacer(Modifier.weight(56f))
        Box {
            LottieAnimation(
                modifier = Modifier
                    .size(323.dp),
                composition = composition,
                progress = { progress },
            )
            Icon(
                painter = painterResource(R.drawable.ic_lottie_arrow),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center),
                tint = Color.Unspecified
            )
        }
        Spacer(Modifier.weight(103f))
        UCompassButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(horizontal = 22.dp),
            text = "접속하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397)
        ) {
            navigateToOnboardingInput()
        }
        Spacer(Modifier.weight(72f))
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen(
        padding = PaddingValues(0.dp),
        navigateToOnboardingInput = {}
    )
}