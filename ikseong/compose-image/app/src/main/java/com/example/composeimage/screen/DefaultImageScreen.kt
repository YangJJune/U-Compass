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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.composeimage.screen.ImageUrl.imageUrls

@Composable
fun DefaultImageUrlScreen(
    innerPadding: PaddingValues,
    imageLoader: ImageLoader,
    navigateToRemoteUrl: () -> Unit = {}
) {
    val state = rememberLazyListState()
    val startTime = remember { mutableLongStateOf(System.currentTimeMillis()) }
    Log.d("DefaultImageUrlScreen", "Start Time: ${startTime.longValue}")

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
                            Log.d("DefaultImageUrlScreen", "End Time: $endTime Source: $source")
                        },
                    )
                    .build(),
                contentDescription = null,
            )
        }
        item {
            Button(onClick = navigateToRemoteUrl) {
                Text("Navigate to Remote Url")
            }
        }
    }

}

@Preview
@Composable
private fun DefaultImageUrlScreenPreview() {
    DefaultImageUrlScreen(
        innerPadding = PaddingValues(0.dp),
        imageLoader = ImageLoader.Builder(LocalContext.current).build(),
    )
}

object ImageUrl {
    val imageUrls = listOf(
        "https://media.istockphoto.com/id/2151693799/ko/%EC%82%AC%EC%A7%84/%EA%B7%80%EC%97%AC%EC%9A%B4-%ED%9D%B0-%EA%B0%95%EC%95%84%EC%A7%80%EA%B0%80-%EA%B1%B0%EC%8B%A4-%EB%B0%94%EB%8B%A5%EC%97%90-%EB%88%84%EC%9B%8C-%EC%9E%88%EC%8A%B5%EB%8B%88%EB%8B%A4.jpg?s=1024x1024&w=is&k=20&c=ubmHHUxLXFehE_9XAE0rJfIDu61_9wMjjXyIpHLwmK4=",
        "https://cdn.pixabay.com/photo/2018/05/26/18/06/dog-3431913_1280.jpg",
        "https://cdn.pixabay.com/photo/2023/09/04/06/59/dog-8232158_1280.jpg",
        "https://cdn.pixabay.com/photo/2023/08/02/14/25/dog-8165447_1280.jpg",
        "https://cdn.pixabay.com/photo/2024/01/15/21/13/puppy-8510899_1280.jpg",
        "https://cdn.pixabay.com/photo/2018/05/11/08/11/dog-3389729_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/06/30/22/34/dog-5357794_1280.jpg",
        "https://cdn.pixabay.com/photo/2014/08/21/14/51/dog-423398_1280.jpg",
        "https://cdn.pixabay.com/photo/2020/07/20/06/42/english-bulldog-5422018_1280.jpg",
        "https://cdn.pixabay.com/photo/2022/12/19/06/31/australian-shepherd-7664795_1280.jpg",
        "https://cdn.pixabay.com/photo/2016/07/15/15/55/dachshund-1519374_1280.jpg",
        "https://cdn.pixabay.com/photo/2022/01/17/19/59/dog-6945696_1280.jpg",
        "https://cdn.pixabay.com/photo/2016/02/19/11/53/pug-1209129_1280.jpg",
    )
}