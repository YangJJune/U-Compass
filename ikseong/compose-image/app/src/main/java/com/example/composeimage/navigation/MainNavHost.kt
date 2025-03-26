package com.example.composeimage.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.request.ImageRequest
import com.example.composeimage.screen.DefaultImageUrlScreen
import com.example.composeimage.screen.ImageSliderScreen
import com.example.composeimage.screen.ImageUrl.imageUrls
import com.example.composeimage.screen.RemoteImageUrlScreen
import com.example.composeimage.viewmodel.ImageViewModel


@Composable
fun MainNavHost(
    innerPadding: PaddingValues,
    imageLoader: ImageLoader
) {
    val navController = rememberNavController()
    val imageViewModel: ImageViewModel = viewModel()

    // pre-load image urls
    imageUrls.forEach {
        val request = ImageRequest.Builder(LocalContext.current)
            .data(it)
            .build()
        imageLoader.enqueue(request)
    }

    NavHost(
        modifier = Modifier.padding(innerPadding),
        navController = navController,
        startDestination = Route.RemoteUrl
    ) {
        composable<Route.DefaultUrl> {
            DefaultImageUrlScreen(
                innerPadding = innerPadding,
                imageLoader = imageLoader,
                navigateToRemoteUrl = { navController.navigate(Route.RemoteUrl) }
            )
        }
        composable<Route.RemoteUrl> {
            RemoteImageUrlScreen(
                innerPadding = innerPadding,
                imageLoader = imageLoader,
                navigateToRemoteUrl = { navController.navigate(Route.DefaultUrl) },
                navigateToImageSlider = { navController.navigate(Route.ImageSlider) },
                viewModel = imageViewModel
            )
        }
        composable<Route.ImageSlider> {
            ImageSliderScreen(
                innerPadding = innerPadding,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
