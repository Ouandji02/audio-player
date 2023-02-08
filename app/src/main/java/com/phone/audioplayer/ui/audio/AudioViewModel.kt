package com.phone.audioplayer.ui.audio

import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phone.audioplayer.domain.model.Audio
import com.phone.audioplayer.domain.repository.AudioRepository
import com.phone.audioplayer.media.constant.K
import com.phone.audioplayer.media.exoPlayer.MediaPlayerServiceConnection
import com.phone.audioplayer.media.exoPlayer.isPlaying
import com.phone.audioplayer.media.services.MediaPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioViewModel(
    val audioRepository: AudioRepository,
    mediaPlayerServiceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    var audioList = mutableListOf<Audio>()
    val currentPlayingAudio = mediaPlayerServiceConnection.currentAudioPlaying
    var isConnected = mediaPlayerServiceConnection.isConnected
    lateinit var rootMediaId: String
    var currentPlayBackPosition by mutableStateOf(0L)
    private var updatePosition = true
    private val playBackState = mediaPlayerServiceConnection.playBackState
     val audioIsPlaying: Boolean
        get() = playBackState.value?.isPlaying == true


    private val subscriptionCallback = object : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
        }
    }
    private val mediaPlayerServiceConnection = mediaPlayerServiceConnection.also { updatePlayBack() }

    var currentDuration = MediaPlayerService.currentDuration
    var currentAudioProgress = mutableStateOf(0f)

    init {
        viewModelScope.launch {
            getDataAndFormat()
            isConnected.collect {
                if (it) {
                    rootMediaId = mediaPlayerServiceConnection.rootMediaId
                    mediaPlayerServiceConnection.playBackState.value?.apply {
                        currentPlayBackPosition = position
                    }
                    mediaPlayerServiceConnection.subscribe(rootMediaId, subscriptionCallback)
                }
            }
        }

    }

    private suspend fun getDataAndFormat() =
        audioRepository.getAudioData().collect { audio ->
            audio.map {
                val displayName = it.displayName.substringBefore(".")
                val artist = if (it.artist.contains("<unknown>")) "Unknown artist" else it.artist
                it.copy(
                    displayName = displayName,
                    artist = artist
                )
            }
            audioList += audio
        }

    fun playAudio(currentAudio: Audio) {
        mediaPlayerServiceConnection.playAudio(audioList)
        if (currentAudio.id == currentPlayingAudio.value?.id) {
            if (audioIsPlaying) mediaPlayerServiceConnection.transportControl.pause()
            else mediaPlayerServiceConnection.transportControl.play()
        } else mediaPlayerServiceConnection.transportControl.playFromMediaId(
            currentAudio.id.toString(),
            null
        )
        println(currentPlayingAudio.value)
    }

    fun stopPlayBack() {
        mediaPlayerServiceConnection.transportControl.stop()
    }

    fun fastForward() {
        mediaPlayerServiceConnection.fastForward()
    }

    fun skipToNext() {
        mediaPlayerServiceConnection.skipNext()
    }

    fun skipToPrevious() {
        mediaPlayerServiceConnection.skipPrevious()
    }

    fun rewind() {
        mediaPlayerServiceConnection.rewind()
    }

    fun seekTo(value: Float) {
        mediaPlayerServiceConnection.transportControl.seekTo(
            (currentDuration * value / 100f).toLong()
        )
    }

    fun updatePlayBack() {
        viewModelScope.launch {
            val position = playBackState.value?.position ?: 0
            if (currentPlayBackPosition != position) currentPlayBackPosition = position
            if (currentDuration > 0) currentAudioProgress.value = (
                    currentPlayBackPosition.toFloat() / currentDuration.toFloat() * 100f
                    )
            delay(K.PLAYBACK_UPDATE_INTERVAL)
            if (updatePosition) updatePlayBack()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerServiceConnection.unSubscribe(
            K.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
        updatePosition = false
    }

}