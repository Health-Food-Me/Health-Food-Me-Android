package org.helfoome.data.service

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.data.model.request.RequestLogin
import timber.log.Timber
import javax.inject.Inject

class KakaoAuthService @Inject constructor(
    @ActivityContext private val context: Context,
    private val client: UserApiClient,
    private val sharedPreferences: HFMSharedPreference,
    private val authService: AuthService
) {
    fun kakaoLogin(loginListener: (() -> Unit)? = null) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Timber.e(error, "카카오계정으로 로그인 실패")
            } else if (token != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching { authService.login(RequestLogin("kakao", token.accessToken)) }
                        .onSuccess {
                            sharedPreferences.isLogin = true
                            val response = it.data
                            with(sharedPreferences) {
                                accessToken = response.accessToken
                                refreshToken = response.refreshToken
                                id = response.user.id
                                nickname = response.user.name
                            }
                            loginListener?.invoke()
                            cancel()
                        }
                        .onFailure {
                            sharedPreferences.isLogin = false
                            cancel()
                        }
                }
            }
        }
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (client.isKakaoTalkLoginAvailable(context)) {
            client.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Timber.e(error, "카카오톡으로 로그인 실패")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    client.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching { authService.login(RequestLogin("kakao", token.accessToken)) }
                            .onSuccess {
                                sharedPreferences.isLogin = true
                                val response = it.data
                                with(sharedPreferences) {
                                    accessToken = response.accessToken
                                    refreshToken = response.refreshToken
                                    id = response.user.id
                                    nickname = response.user.name
                                }
                                loginListener?.invoke()
                                cancel()
                            }
                            .onFailure {
                                sharedPreferences.isLogin = false
                                cancel()
                            }
                    }
                }
            }
        } else {
            client.loginWithKakaoAccount(context, callback = callback)
        }
    }
}
