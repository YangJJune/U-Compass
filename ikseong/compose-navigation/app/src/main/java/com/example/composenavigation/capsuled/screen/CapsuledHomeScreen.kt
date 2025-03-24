package com.example.composenavigation.capsuled.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CapsuledHomeRoute(
    padding: PaddingValues,
    navigateToProfile: (String) -> Unit,
    navigateToCapsuled: () -> Unit,
    navigateToLanding: () -> Unit,
) {
    CapsuledHomeScreen(
        padding = padding,
        navigateToProfile = navigateToProfile,
        navigateToCapsuled = navigateToCapsuled,
        navigateToLanding = navigateToLanding,
    )
}

@Composable
fun CapsuledHomeScreen(
    padding: PaddingValues,
    navigateToProfile: (String) -> Unit,
    navigateToCapsuled: () -> Unit,
    navigateToLanding: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "Home",
                fontSize = 48.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = { navigateToProfile("Ikseong Jo") },
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Profile")
            }

            Button(
                onClick = navigateToCapsuled,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Capsuled")
            }

            Button(
                onClick = navigateToLanding,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Landing")
            }
        }
    }
}