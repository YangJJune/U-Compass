package com.ikseong.ucompass.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ikseong.ucompass.R

val fontExtraBold = FontFamily(Font(R.font.pretendard_extrabold))
val fontBold = FontFamily(Font(R.font.pretendard_bold))
val fontSemiBold = FontFamily(Font(R.font.pretendard_semibold))
val fontRegular = FontFamily(Font(R.font.pretendard_regular))
val fontMedium = FontFamily(Font(R.font.pretendard_medium))
val fontBlack = FontFamily(Font(R.font.pretendard_black))
val fontLight = FontFamily(Font(R.font.pretendard_light))
val fontThin = FontFamily(Font(R.font.pretendard_thin))

@Immutable
data class UCompassTypography(
    val bold: TextStyle,
    val semiBold: TextStyle,
    val medium: TextStyle,
    val regular: TextStyle,
)

val defaultUCompassTypography = UCompassTypography(
    bold = TextStyle(
        fontFamily = fontBold,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    ),
    semiBold = TextStyle(
        fontFamily = fontSemiBold,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    ),
    medium = TextStyle(
        fontFamily = fontMedium,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
    ),
    regular = TextStyle(
        fontFamily = fontRegular,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
)

val LocalUCompassTypographyProvider = staticCompositionLocalOf { defaultUCompassTypography }
