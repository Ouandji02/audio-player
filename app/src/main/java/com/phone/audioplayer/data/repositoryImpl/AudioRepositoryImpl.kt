package com.phone.audioplayer.data.repositoryImpl

import com.phone.audioplayer.data.ContentResolverHelper
import com.phone.audioplayer.domain.model.Audio
import com.phone.audioplayer.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRepositoryImpl(private val contentResolverHelper: ContentResolverHelper) : AudioRepository {
    override suspend fun getAudioData() = flow {
        try {
            val data = contentResolverHelper.getAudioData()
            emit(data)
        }catch (_: Exception){
            emit(listOf<Audio>())
        }
    }

}