package org.helfoome.data.service

import android.content.Context
import android.content.Intent
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import org.helfoome.BuildConfig.*
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.request.RequestLogin
import org.helfoome.presentation.MainActivity
import timber.log.Timber
import javax.inject.Inject
import org.helfoome.util.ext.startActivity

class NaverAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: HFMSharedPreference,
    private val authService: AuthService
) : OAuthLoginCallback {
    var loginListener : (() -> Unit)? = null

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
            runCatching { authService.login(RequestLogin("naver", NaverIdLoginSDK.getAccessToken().toString())) }
                .onSuccess {
                    sharedPreferences.accessToken = it.data.accessToken
                    loginListener?.invoke()
                    cancel()
                }
                .onFailure {
                    cancel()
                }
        }

        Timber.i(NaverIdLoginSDK.getAccessToken().toString())
        Timber.i(NaverIdLoginSDK.getRefreshToken().toString())
    }
}
