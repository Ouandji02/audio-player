package com.phone.audioplayer.media.exoPlayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.phone.audioplayer.R
import com.phone.audioplayer.media.constant.K

internal class NotificationManager(
    context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: NotificationListener
) {
    private lateinit var notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        val builder = PlayerNotificationManager.Builder(
            context,
            K.PLAYBACK_NOTIFICATION_ID,
            K.PLAYBACK_NOTIFICATION_CHANNEL_ID
        )

        builder.apply {
            setNotificationListener(notificationListener)
            setChannelDescriptionResourceId(androidx.appcompat.R.string.abc_action_bar_home_description)
            setChannelNameResourceId(com.google.android.exoplayer2.R.string.exo_download_notification_channel_name)
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
        }

        notificationManager = builder.build()

        notificationManager.apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.music_icon)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
        }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            controller.metadata.description.title.toString()

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player): CharSequence? =
            controller.metadata.description.subtitle

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? = null

    }
}