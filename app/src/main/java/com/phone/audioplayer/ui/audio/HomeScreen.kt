package com.phone.audioplayer.ui.audio

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.phone.audioplayer.domain.model.Audio
import com.phone.audioplayer.ui.theme.Shapes
import kotlin.math.floor


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    audioList: List<Audio>,
    progress: Float,
    isAudioPlaying: Boolean,
    onProgressChange: (Float) -> Unit,
    onNext: () -> Unit,
    onStart: (Audio) -> Unit,
    currentPlayingAudio: Audio?,
    onClickItem: (Audio) -> Unit
) {
    println("dhhhhhhhhhhhhhhhj" + currentPlayingAudio)
    val scaffoldState = rememberBottomSheetScaffoldState()
    val animatedHeight by animateDpAsState(
        targetValue = if (currentPlayingAudio == null) 0.dp
        else BottomSheetScaffoldDefaults.SheetPeekHeight
    )
    BottomSheetScaffold(
        backgroundColor = MaterialTheme.colors.primaryVariant,
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedHeight,
        sheetContent = {
            currentPlayingAudio?.let { audio ->
                BottomBar(
                    progress = progress,
                    onProgressChange = { onProgressChange.invoke(it) },
                    onStart = {
                        onStart.invoke(audio)
                    },
                    onNext = { onNext.invoke() },
                    audio = audio,
                    isAudioPlaying = isAudioPlaying
                )
            }
        },
        sheetBackgroundColor = MaterialTheme.colors.primary
    ) {
        ListAudio(audioList = audioList, onClickItem = onClickItem)
    }
}

@Composable
fun BottomBar(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onStart: () -> Unit,
    onNext: () -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean
) {
    val colors = MaterialTheme.colors
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    border = BorderStroke(3.dp, Color.White),
                    shape = CircleShape,
                    contentColor = Color.White,
                    modifier = Modifier.padding(end = 10.dp),
                    color = colors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "icons",
                        modifier = Modifier
                            .padding(5.dp)
                            .size(30.dp),
                        tint = Color.White
                    )
                }
                Column() {
                    Text(
                        text = audio.displayName.apply {
                            substring(
                                0,
                                if (this.length > 30) 30 else this.length
                            )
                        },
                        style = MaterialTheme.typography.subtitle1.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        audio.artist,
                        style = MaterialTheme.typography.subtitle2.copy(color = Color.White)
                    )
                }
            }
            Row() {
                IconButton(onClick = onStart) {
                    Icon(
                        imageVector = if (!isAudioPlaying) Icons.Default.PlayCircleFilled
                        else Icons.Default.PauseCircleFilled,
                        tint = colors.onSurface,
                        contentDescription = "icons",
                        modifier = Modifier.size(30.dp)
                    )
                }
                IconButton(
                    onClick = onNext, modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "icons",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
        Slider(
            value = progress,
            onValueChange = onProgressChange,
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colors.onSurface,
                activeTickColor = colors.onSurface,
                activeTrackColor = colors.onSurface
            )
        )
    }
}

@Composable
fun ListAudio(audioList: List<Audio>, onClickItem: (Audio) -> Unit) {
    LazyColumn {
        items(audioList) {
            AudioItem(audio = it, onClickItem = onClickItem)
        }
    }
}

@Composable
fun AudioItem(audio: Audio, onClickItem: (Audio) -> Unit) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .padding(vertical = 7.dp, horizontal = 10.dp)
            .clickable {
                onClickItem.invoke(audio)
            },
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = audio.displayName.apply {
                        substring(
                            0,
                            if (this.length > 30) 30 else this.length
                        )
                    },
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    audio.artist,
                    style = MaterialTheme.typography.subtitle2.copy(color = Color.White)
                )
            }
            Text(text = timeFormat(audio.duration))
        }
    }

}


private fun timeFormat(position: Int): String {
    val totalSeconds = floor(position / 1E3).toInt()
    val minute = totalSeconds / 60
    val remainingSecond = totalSeconds - (minute * 60)
    return if (position < 0) "--:--" else "%d:%02d".format(minute, remainingSecond)
}
