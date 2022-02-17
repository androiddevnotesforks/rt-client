package com.automotivecodelab.rtclient

import android.app.Application
import android.content.Context
import com.automotivecodelab.rtclient.di.ApplicationComponent
import com.automotivecodelab.rtclient.di.DaggerApplicationComponent
import timber.log.Timber

class MyApplication : Application() {

    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appComponent = DaggerApplicationComponent
            .factory()
            .create(this)
    }
}

val Context.AppComponent: ApplicationComponent
    get() = (applicationContext as MyApplication).appComponent
