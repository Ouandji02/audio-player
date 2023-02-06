package com.phone.audioplayer.media.exoPlayer

import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.phone.audioplayer.domain.repository.AudioRepository

class MediaSource(private val audioRepository: AudioRepository) {
    private val onReadyListeners: MutableList<OnReadyListener> = mutableListOf()
    var audioMediaMetaData: List<MediaMetadataCompat> = emptyList()
    private var state: AudioSourceState = AudioSourceState.STATE_CREATED
        set(value) {
            if (value == AudioSourceState.STATE_CREATED || value == AudioSourceState.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it.invoke(isReady)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(listener: OnReadyListener): Boolean {
        return if (state == AudioSourceState.STATE_CREATED || state == AudioSourceState.STATE_INITIALIZING) {
            onReadyListeners += listener
            false
        } else {
            listener.invoke(isReady)
            true
        }
    }

    suspend fun load() {
        state = AudioSourceState.STATE_INITIALIZING
        audioRepository.getAudioData().collect { data ->
            audioMediaMetaData = data.map { audio ->
                MediaMetadataCompat.Builder().putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                    audio.id.toString()
                ).putString(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                    audio.artist
                ).putString(
                    MediaMetadataCompat.METADATA_KEY_TITLE,
                    audio.title
                ).putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    audio.duration.toLong()
                ).putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                    audio.displayName
                ).putString(
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    audio.uri.toString()
                ).build()
            }
        }
        state = AudioSourceState.STATE_INITIALIZED
    }

    fun asMediaSource(dataSource: CacheDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()

        audioMediaMetaData.forEach {
            val mediaItem = MediaItem.fromUri(
                it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
            )

            val mediaSource = ProgressiveMediaSource
                .Factory(dataSource)
                .createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItem() = audioMediaMetaData.map {
        val description = MediaDescriptionCompat.Builder()
            .setDescription(it.description.description)
            .setMediaId(it.description.mediaId)
            .setMediaUri(it.description.mediaUri)
            .setTitle(it.description.title)
            .setSubtitle(it.description.subtitle)
            .build()
        MediaBrowserCompat.MediaItem(description, FLAG_PLAYABLE)
    }.toMutableList()

    fun refresh() {
        onReadyListeners.clear()
        state = AudioSourceState.STATE_CREATED
    }

    private val isReady: Boolean
        get() = state == AudioSourceState.STATE_CREATED
}

enum class AudioSourceState {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR,
}
typealias OnReadyListener = (Boolean) -> Unit