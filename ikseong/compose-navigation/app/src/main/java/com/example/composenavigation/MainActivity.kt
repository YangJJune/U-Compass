package com.example.composenavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.example.composenavigation.bottomnav.BottomBar
import com.example.composenavigation.bottomnav.BottomNavController
import com.example.composenavigation.bottomnav.BottomNavHost
import com.example.composenavigation.bottomnav.BottomTab
import com.example.composenavigation.bottomnav.rememberBottomNavigator
import com.example.composenavigation.ui.theme.ComposeNavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNavigationTheme {
                val navigator: BottomNavController = rememberBottomNavigator()

                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomBar(
                            modifier = Modifier
                                .background(Color.White)
                                .navigationBarsPadding(),
                            visible = navigator.shouldShowBottomBar(),
                            tabs = BottomTab.entries,
                            currentTab = navigator.currentTab,
                            onTabSelected = { navigator.navigate(it) }
                        )
                    }) { innerPadding ->
//                    SimpleNavHost(innerPadding)
//                    NestedNavHost(innerPadding)
//                    CapsuledNavHost(innerPadding)
                    BottomNavHost(
                        navigator = navigator,
                        padding = innerPadding
                    )

                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeNavigationTheme {
        Box {
            Image(bitmap = ImageBitmap(0, 0), contentDescription = "Artist image")
            Icon(Icons.Filled.Check, contentDescription = "Check mark")
        }
    }
}