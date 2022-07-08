package org.helfoome.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import org.helfoome.BuildConfig.*
import org.helfoome.R
import org.helfoome.databinding.ActivityLoginBinding
import org.helfoome.util.binding.BindingActivity
import timber.log.Timber

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.ivNaverLogin.setOnClickListener {
            naverLogin()
        }

        binding.ivKakaoLogin.setOnClickListener {
            kakaoLogin()
        }
    }

    private fun kakaoLogin() {
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Timber.tag(TAG).e(error, "카카오계정으로 로그인 실패")
            } else if (token != null) {
                Timber.tag(TAG).i("카카오계정으로 로그인 성공 ${token.accessToken}")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Timber.tag(TAG).e(error, "카카오톡으로 로그인 실패")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Timber.tag(TAG).i("카카오톡으로 로그인 성공 ${token.accessToken}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun naverLogin() {
        NaverIdLoginSDK.initialize(this, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, NAVER_CLIENT_NAME)

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Timber.tag(TAG).d(NaverIdLoginSDK.getAccessToken().toString())
                Timber.tag(TAG).d(NaverIdLoginSDK.getRefreshToken().toString())
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@LoginActivity, "errorCode:$errorCode, errorDesc:$errorDescription", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }
        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
