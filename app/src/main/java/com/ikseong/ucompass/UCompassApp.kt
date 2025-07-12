package com.ikseong.ucompass

import android.app.Application
import com.ikseong.ucompass.analytics.AnalyticsManager
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class UCompassApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(BuildConfig.NAVER_MAP_CLIENT_ID)

        AnalyticsManager.initialize()

    }
}