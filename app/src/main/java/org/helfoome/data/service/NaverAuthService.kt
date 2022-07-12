package org.helfoome.data.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.qualifiers.ActivityContext
import org.helfoome.BuildConfig.*
import timber.log.Timber
import javax.inject.Inject

class NaverAuthService @Inject constructor(@ActivityContext private val context: Context) : OAuthLoginCallback {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess get() = _loginSuccess

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
        _loginSuccess.value = true
        Timber.i(NaverIdLoginSDK.getAccessToken().toString())
        Timber.i(NaverIdLoginSDK.getRefreshToken().toString())
    }
}
