package com.phone.audioplayer.domain.repository

import com.phone.audioplayer.domain.model.Audio
import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    suspend fun getAudioData() : Flow<List<Audio>>
}