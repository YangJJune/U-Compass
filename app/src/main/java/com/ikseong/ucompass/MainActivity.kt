package com.ikseong.ucompass

import SocketRepository
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ikseong.ucompass.ui.navigation.MainNavHost
import com.ikseong.ucompass.ui.theme.UCompassTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val socketRepo = SocketRepository(onReceive = {
            res -> when(res.optString("type")){
                "location_broadcast" -> {
                    val userId = res.optString("user_id")
                    val lat = res.optDouble("lat")
                    val lng = res.optDouble("lng")
                    Log.d("Socket", "위치 수신: $userId at ($lat, $lng)")
                }

                "status" -> {
                    val status = res.optString("status")
                    Log.d("Socket", "로그인 결과: $status")
                }

                else -> {
                    Log.w("Socket", "알 수 없는 메시지: $res")
                }
            }
        })
        lifecycleScope.launch {
            socketRepo.connect()
            socketRepo.login("testId", 1)
            socketRepo.sendLocation(37.5, 127.0)
        }

        enableEdgeToEdge()
        setContent {
            UCompassTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(
                        padding = innerPadding,
                        navController = navController
                    )
                }
            }
        }
    }
}

