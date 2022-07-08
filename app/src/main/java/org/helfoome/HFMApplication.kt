package org.helfoome

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.helfoome.BuildConfig.KAKAO_NATIVE_KEY
import org.helfoome.util.HFMDebugTree
import timber.log.Timber

@HiltAndroidApp
class HFMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, KAKAO_NATIVE_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(HFMDebugTree())
        }
    }
}
