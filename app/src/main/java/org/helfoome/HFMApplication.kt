package org.helfoome

import android.app.Application
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import org.helfoome.BuildConfig.NAVER_API_MAP_KEY
import org.helfoome.util.HFMDebugTree
import timber.log.Timber

@HiltAndroidApp
class HFMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_API_MAP_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(HFMDebugTree())
        }
    }
}
