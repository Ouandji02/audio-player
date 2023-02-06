package com.phone.audioplayer

import android.app.Application
import com.phone.audioplayer.di.mediaModule
import com.phone.audioplayer.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EntryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@EntryApp)
            modules(mediaModule, serviceModule)
        }
    }
}