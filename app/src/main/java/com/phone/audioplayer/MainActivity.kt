package com.phone.audioplayer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.phone.audioplayer.domain.model.Audio
import com.phone.audioplayer.ui.audio.AudioViewModel
import com.phone.audioplayer.ui.audio.HomeScreen
import com.phone.audioplayer.ui.theme.AudioPlayerTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

val audios = listOf(
    Audio(
        uri = Uri.parse(""),
        displayName = "Garde du coeur",
        artist = "Dadju",
        title = "Garde du coeur",
        id = 12545,
        duration = 125,
        data = ""
    ),
    Audio(
        uri = Uri.parse(""),
        displayName = "Garde du coeur",
        artist = "Dadju",
        title = "Garde du coeur",
        id = 12545,
        duration = 125,
        data = ""
    )
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioPlayerTheme (darkTheme = false){
                val permissionState =
                    rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (Lifecycle.Event.ON_RESUME == event) permissionState.launchPermissionRequest()
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    if (permissionState.hasPermission) {
                        val audioViewModel = getViewModel<AudioViewModel>()
                        val audioList = audioViewModel.audioList
                        HomeScreen(
                            audioList = audioList,
                            progress = audioViewModel.currentAudioProgress.value,
                            isAudioPlaying = audioViewModel.audioIsPlaying,
                            onProgressChange = { audioViewModel.seekTo(it) },
                            onNext = { audioViewModel.skipToNext() },
                            onStart = { audioViewModel.playAudio(it) },
                            currentPlayingAudio = audioViewModel.currentPlayingAudio.value ,
                            onClickItem = { audioViewModel.playAudio(it) }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Grant permission first to use this app")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AudioPlayerTheme {
        Greeting("Android")
    }
}