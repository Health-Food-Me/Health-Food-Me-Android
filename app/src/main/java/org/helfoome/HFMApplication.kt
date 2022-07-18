package org.helfoome

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import org.helfoome.BuildConfig.KAKAO_NATIVE_KEY
import com.naver.maps.map.NaverMapSdk
import org.helfoome.BuildConfig.NAVER_API_MAP_KEY
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.util.HFMDebugTree
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class HFMApplication : Application() {
    @Inject lateinit var hfmSharedPreference: HFMSharedPreference

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, KAKAO_NATIVE_KEY)
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(NAVER_API_MAP_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(HFMDebugTree())
        }
    }
}
