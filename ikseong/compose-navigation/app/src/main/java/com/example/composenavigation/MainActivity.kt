package com.example.composenavigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.example.composenavigation.capsuled.CapsuledNavHost
import com.example.composenavigation.ui.theme.ComposeNavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNavigationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    SimpleNavHost(innerPadding)
//                    NestedNavHost(innerPadding)
                    CapsuledNavHost(innerPadding)

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