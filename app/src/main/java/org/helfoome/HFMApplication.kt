package org.helfoome

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.helfoome.util.HFMDebugTree
import timber.log.Timber

@HiltAndroidApp
class HFMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(HFMDebugTree())
        }
    }
}
