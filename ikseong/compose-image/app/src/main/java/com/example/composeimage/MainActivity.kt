package com.example.composeimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.imageLoader
import com.example.composeimage.navigation.MainNavHost
import com.example.composeimage.ui.theme.ComposeImageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeImageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(innerPadding = innerPadding, imageLoader = imageLoader)
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainPreview() {
    ComposeImageTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainNavHost(
                innerPadding = innerPadding,
                imageLoader = ImageLoader(LocalContext.current)
            )
        }
    }
}