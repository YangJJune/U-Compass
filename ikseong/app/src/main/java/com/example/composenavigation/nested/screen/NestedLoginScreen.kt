package com.example.composenavigation.nested.screen

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
fun NestedLoginRoute(
    padding: PaddingValues,
    navigateToLanding: () -> Unit,
    navigateToOnboarding: () -> Unit,
    navigateToHome: () -> Unit,
) {
    NestedLoginScreen(
        padding = padding,
        navigateToLanding = navigateToLanding,
        navigateToOnboarding = navigateToOnboarding,
        navigateToHome = navigateToHome,
    )
}

@Composable
fun NestedLoginScreen(
    padding: PaddingValues,
    navigateToLanding: () -> Unit,
    navigateToOnboarding: () -> Unit,
    navigateToHome: () -> Unit,
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
                text = "Login",
                fontSize = 48.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = navigateToOnboarding,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Onboarding")
            }

            Button(
                onClick = navigateToLanding,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Landing")
            }
            Button(
                onClick = navigateToHome,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Go to Home")
            }

        }
    }
}