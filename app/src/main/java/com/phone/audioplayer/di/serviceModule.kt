package com.phone.audioplayer.di

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.C.USAGE_MEDIA
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.phone.audioplayer.media.exoPlayer.MediaSource
import org.koin.dsl.module
import java.io.File

fun provideAudioAttribute(): AudioAttributes =
    AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(USAGE_MEDIA).build()

fun provideExoPlayer(context: Context, audioAttributes: AudioAttributes): ExoPlayer =
    ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
    }

fun provideDataSourceFactory(context: Context) = DefaultDataSource.Factory(context)

fun provideCacheDataSourceFactory(
    context: Context,
    dataSource: DefaultDataSource.Factory
): CacheDataSource.Factory {
    val cacheDir = File(context.cacheDir, "media")
    val databaseProvider = StandaloneDatabaseProvider(context)
    val cache = SimpleCache(cacheDir, NoOpCacheEvictor(), databaseProvider)
    return CacheDataSource.Factory().apply {
        setCache(cache)
        setUpstreamDataSourceFactory(dataSource)
    }

}

val serviceModule = module {
   single {
       provideAudioAttribute()
   }
    single {
        provideExoPlayer(get(), get())
    }

    single {
        MediaSource(get())
    }
    factory {
        provideDataSourceFactory(get())
    }
    factory {
        provideCacheDataSourceFactory(get(), get())
    }
}
