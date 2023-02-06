package com.phone.audioplayer.media.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telecom.Connection
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.phone.audioplayer.media.constant.K
import com.phone.audioplayer.media.exoPlayer.MediaSource
import com.phone.audioplayer.media.exoPlayer.NotificationManager

class MediaPlayerService : MediaBrowserServiceCompat() {

    lateinit var dataSourceFactory: CacheDataSource.Factory

    lateinit var exoPlayer: ExoPlayer

    lateinit var mediaSource : MediaSource

    lateinit var mediaSessionCompat: MediaSessionCompat

    lateinit var mediaSessionConnection: MediaSessionConnector

    private lateinit var mediaPlayerNotificationManager : NotificationManager

    private var currentPlayingMedia : MediaMetadataCompat? = null

    private var isPlayerInitialized = false

     var isForegroundService : Boolean = false

    companion object{
        private val TAG = "MediaPlayerService"

        var currentDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val sessionActivityIntent = packageManager
            .getLaunchIntentForPackage(packageName)
            ?.let {
                PendingIntent.getActivity(
                    this,
                    0,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        }

        mediaSessionCompat = MediaSessionCompat(this,TAG).apply {
            setSessionActivity(sessionActivityIntent)
            isActive = true
        }

        sessionToken = mediaSessionCompat.sessionToken

        mediaPlayerNotificationManager = NotificationManager(this, mediaSessionCompat.sessionToken,)
    }
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


    inner class PlayerNotificationListener : PlayerNotificationManager.NotificationListener{
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing  && !isForegroundService){
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(
                        applicationContext,
                        this@MediaPlayerService.javaClass
                    )
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }
    }

}