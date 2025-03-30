package com.example.composeimage.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.composeimage.viewmodel.ImageViewModel

@Composable
fun RemoteImageUrlScreen(
    innerPadding: PaddingValues,
    imageLoader: ImageLoader,
    navigateToRemoteUrl: () -> Unit = {},
    navigateToImageSlider: () -> Unit = {},
    viewModel : ImageViewModel = viewModel()
) {
    val state = rememberLazyListState()
    val startTime = remember { mutableLongStateOf(System.currentTimeMillis()) }
    Log.d("RemoteImageUrlScreen", "Start Time: ${startTime.longValue}")

    val imageUrls by viewModel.images.collectAsStateWithLifecycle()

    LazyColumn(
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(imageUrls.size) { index ->
            AsyncImage(
                modifier = Modifier.size(100.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrls[index])
                    .crossfade(true)
                    .listener(
                        onSuccess = { _, result ->
                            val endTime = System.currentTimeMillis() - startTime.longValue
                            val source = result.dataSource
                            Log.d("RemoteImageUrlScreen", "End Time: $endTime Source: $source")
                        },
                    )
                    .build(),
                contentDescription = null,
            )
        }
        item {
            Button(onClick = navigateToRemoteUrl) {
                Text("Navigate to Default Url")
            }
        }
        item {
            Button(onClick = navigateToImageSlider) {
                Text("Navigate to Image Slider")
            }
        }
    }
}

@Preview
@Composable
private fun RemoteImageUrlScreenPreview() {
    RemoteImageUrlScreen(
        innerPadding = PaddingValues(0.dp),
        imageLoader = ImageLoader.Builder(LocalContext.current).build()
    )
}
