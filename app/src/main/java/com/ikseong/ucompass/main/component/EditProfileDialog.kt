package com.ikseong.ucompass.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.ikseong.ucompass.R
import com.ikseong.ucompass.common.component.UCompassButton
import com.ikseong.ucompass.common.component.UCompassTextField
import com.ikseong.ucompass.ui.theme.UCompassTheme.typography
import com.ikseong.ucompass.util.viewutil.noRippleClickable

@Composable
fun EditProfileDialog(
    modifier: Modifier = Modifier,
    profileImgUrl: String = "",
    onComplete: (String, String) -> Unit = { _, _ -> },
    onEditProfileImgClick: () -> Unit = { },
    onDismissRequest: () -> Unit = { },
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .align(Alignment.CenterEnd)
                        .noRippleClickable { onDismissRequest() }
                ) {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Center),
                        imageVector = Icons.Filled.Close,
                        contentDescription = "닫기",
                        tint = Color(0xFF808080)
                    )
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp),
                    text = "프로필 수정",
                    style = typography.semiBold.copy(
                        fontSize = 24.sp,
                    )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .noRippleClickable { onEditProfileImgClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 15.dp, end = 18.dp)
                            .size(116.dp)
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
                            .size(43.dp)
                            .clip(CircleShape)
                            .background(color = Color(0xFFCCF9EA))
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(R.drawable.ic_edit_profile_camera),
                            contentDescription = "프로필 이미지 편집 아이콘",
                            tint = Color(0xFF606060)
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .padding(start = 15.dp, top = 24.dp),
                    text = "이름",
                    style = typography.medium.copy(
                        fontSize = 13.sp,
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
                UCompassButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    text = "수정 완료",
                    fontSize = 20.sp,
                    color = Color(0xFF00E397),
                    contentPadding = PaddingValues(vertical = 19.dp),
                ) { onComplete(name, email) }
            }
        }
    }
}

@Preview
@Composable
private fun EditProfileDialogPreview() {
    EditProfileDialog()
}