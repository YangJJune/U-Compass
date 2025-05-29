package com.ikseong.ucompass.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ikseong.ucompass.R
import com.ikseong.ucompass.ui.common.component.UCompassButton
import com.ikseong.ucompass.ui.common.component.UCompassLogo
import com.ikseong.ucompass.ui.common.component.UCompassTextField
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import com.ikseong.ucompass.ui.util.viewutil.noRippleClickable

@Composable
fun OnboardingInputRoute(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
) {
    OnboardingInsertScreen(
        padding = padding,
        navigateToHome = navigateToHome
    )
}

@Composable
fun OnboardingInsertScreen(
    padding: PaddingValues,
    navigateToHome: () -> Unit,
    profileImgUrl: String = "",
    onEditProfileImgClick: () -> Unit = { },
    onInsertClick: (String, String) -> Unit = { name, email ->
        navigateToHome()
    }
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(32f))
        UCompassLogo()
        Spacer(Modifier.weight(42f))
        Text(
            text = "정보를 입력해주세요.",
            style = typography.semiBold.copy(
                fontSize = 32.sp
            )
        )
        Spacer(Modifier.weight(20f))
        Text(
            text = "서비스 사용을 위해 정보들을 입력해주세요.",
            style = typography.medium.copy(
                fontSize = 18.sp,
                color = Color(0xFF606060)
            )
        )
        Spacer(Modifier.weight(30f))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 35.dp),
            color = Color(0xFFD9D9D9)
        )
        Spacer(Modifier.weight(30f))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .noRippleClickable { onEditProfileImgClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 15.dp, end = 18.dp)
                    .size(170.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(color = Color(0xFF00E397))
            ) {
                AsyncImage(
                    modifier = Modifier.matchParentSize(),
                    model = profileImgUrl,
                    contentDescription = "프로필 이미지"
                )
            }
            Box(
                modifier = Modifier
                    .size(63.dp)
                    .clip(CircleShape)
                    .background(color = Color(0xFFCCF9EA))
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(35.dp),
                    painter = painterResource(R.drawable.ic_edit_profile_camera),
                    contentDescription = "프로필 이미지 편집 아이콘",
                    tint = Color(0xFF606060)
                )
            }
        }
        Spacer(Modifier.weight(30f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 15.dp, top = 24.dp),
                text = "이름",
                style = typography.medium.copy(
                    fontSize = 13.sp,
                    color = Color(0x80000000)
                )
            )
            UCompassTextField(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                verticalPadding = 12.dp,
                text = name,
                style = typography.medium.copy(
                    fontSize = 14.sp,
                    color = Color(0x80000000)
                ),
                placeholder = "이름을 입력해주세요.",
                onValueChange = { name = it }
            )
            Text(
                modifier = Modifier
                    .padding(start = 15.dp, top = 12.dp),
                text = "메일주소",
                style = typography.medium.copy(
                    fontSize = 13.sp,
                    color = Color(0x80000000)
                )
            )
            UCompassTextField(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                verticalPadding = 12.dp,
                text = email,
                style = typography.medium.copy(
                    fontSize = 14.sp,
                    color = Color(0x80000000)
                ),
                placeholder = "이메일을 입력해주세요.",
                onValueChange = { email = it }
            )
        }
        Spacer(Modifier.weight(105f))
        UCompassButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            text = "등록하기",
            fontSize = 20.sp,
            color = Color(0xFF00E397),
            contentPadding = PaddingValues(vertical = 19.dp),
        ) { onInsertClick(name, email) }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingInsertScreenPreview() {
    OnboardingInsertScreen(
        padding = PaddingValues(0.dp),
        navigateToHome = { },
        profileImgUrl = "https://example.com/profile.jpg",
        onEditProfileImgClick = { },
        onInsertClick = { _, _ -> }
    )
}