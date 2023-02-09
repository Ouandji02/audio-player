package com.phone.audioplayer.media.exoPlayer

import android.app.Notification
import android.content.Context
import android.media.session.MediaSession
import com.phone.audioplayer.R
import com.phone.audioplayer.media.constant.K

class NotificationManagerCustomize(
    mediaSessionToken : MediaSession.Token,
    context : Context
) {

    val mediaStyle = Notification.MediaStyle().setMediaSession(mediaSessionToken)
    val notification = Notification.Builder(context, K.PLAYBACK_NOTIFICATION_CHANNEL_ID)
        .setStyle(mediaStyle)
        .setSmallIcon(R.drawable.music_icon)
        .build()

    // Specify any actions which your users can perform, such as pausing and skipping to the next track.
}