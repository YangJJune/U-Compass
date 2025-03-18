package com.example.composenavigation.bottomnav

import com.example.composenavigation.R
import kotlinx.serialization.Serializable

@Serializable
enum class BottomTab(
    val label: String,
    val route: BottomNavRoute,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val description : String = ""
) {
    HOME("홈", BottomNavRoute.BottomHome, R.drawable.ic_home, R.drawable.ic_home, "홈 화면"),
    ASSETS("자산", BottomNavRoute.BottomAssets, R.drawable.ic_asset, R.drawable.ic_asset, "자산 관리"),
    RECORDS("가계부", BottomNavRoute.BottomRecords, R.drawable.ic_calendar, R.drawable.ic_calendar, "가계부"),
    HEALTH("건강", BottomNavRoute.BottomHealth, R.drawable.ic_health, R.drawable.ic_health, "건강"),
    SHOPPING("쇼핑", BottomNavRoute.BottomShopping, R.drawable.ic_bag, R.drawable.ic_bag, "쇼핑")
}

