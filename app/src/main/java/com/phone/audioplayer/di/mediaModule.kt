package com.phone.audioplayer.di

import com.phone.audioplayer.data.ContentResolverHelper
import com.phone.audioplayer.data.repositoryImpl.AudioRepositoryImpl
import com.phone.audioplayer.domain.repository.AudioRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mediaModule = module {
    single{
        ContentResolverHelper(androidContext())
    }
    single<AudioRepository> {
        AudioRepositoryImpl(get())
    }
}
