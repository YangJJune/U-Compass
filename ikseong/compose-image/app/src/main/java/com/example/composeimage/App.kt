package com.example.composeimage

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.memory.MemoryCache
import coil3.util.DebugLogger

class App: Application(), SingletonImageLoader.Factory  {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .logger(DebugLogger())
            .memoryCache(
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.35)
                    .build()
            )
            .build()
    }
}