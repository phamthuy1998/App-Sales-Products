package com.thuypham.ptithcm.mytiki

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessaging
import com.thuypham.ptithcm.mytiki.di.appModule
import com.thuypham.ptithcm.mytiki.util.Constant
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication: Application(), LifecycleObserver {

    companion object {
        lateinit var instance: MainApplication
            private set
    }

    override fun onCreate() {
        FirebaseMessaging.getInstance().subscribeToTopic(Constant.TOPIC_NEW_PRODUCT)
        super.onCreate()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule)
        }
    }
}