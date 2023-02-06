package com.phone.audioplayer.media.services

import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.phone.audioplayer.media.constant.K
import javax.sql.DataSource

class MediaPlayerService : MediaBrowserServiceCompat() {

    lateinit var dataSourceFactory: CacheDataSource.Factory

    lateinit var exoPlayer: ExoPlayer

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
       return BrowserRoot(K.MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

}