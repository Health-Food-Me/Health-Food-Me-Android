package org.helfoome.data.service

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.helfoome.BuildConfig.*
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.request.RequestLogin
import org.helfoome.util.DeviceInfo
import timber.log.Timber
import javax.inject.Inject

class NaverAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: HFMSharedPreference,
    private val authService: AuthService,
    private val deviceInfo: DeviceInfo,
) : OAuthLoginCallback {
    var loginListener: (() -> Unit)? = null

    init {
        NaverIdLoginSDK.initialize(context, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, NAVER_CLIENT_NAME)
    }

    override fun onError(errorCode: Int, message: String) {
        onFailure(errorCode, message)
        NaverIdLoginSDK
    }

    override fun onFailure(httpStatus: Int, message: String) {
        Timber.i(NaverIdLoginSDK.getLastErrorCode().code)
        Timber.i(NaverIdLoginSDK.getLastErrorDescription())
    }

    override fun onSuccess() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                authService.login(
                    RequestLogin(
                        "naver",
                        NaverIdLoginSDK.getAccessToken().toString(),
                        "AOS; ${deviceInfo.getDeviceOs()}; ${deviceInfo.getDeviceModel()}; ${deviceInfo.getAppVersion()}"
                    )
                )
            }
                .onSuccess {
                    with(sharedPreferences) {
                        isLogin = true
                        accessToken = it.data.accessToken
                        refreshToken = it.data.refreshToken
                        id = it.data.user.id
                        nickname = it.data.user.name
                    }
                    loginListener?.invoke()
                    Timber.d(it.message)
                    cancel()
                }
                .onFailure {
                    Timber.d(it.message)
                    cancel()
                }
        }
        Timber.i(NaverIdLoginSDK.getAccessToken().toString())
        Timber.i(NaverIdLoginSDK.getRefreshToken().toString())
    }
}
