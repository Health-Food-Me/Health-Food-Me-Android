package org.helfoome.data.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import org.helfoome.BuildConfig.*
import org.helfoome.HFMApplication
import org.helfoome.data.local.HFMSharedPreference
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

class NaverAuthService @Inject constructor(@ApplicationContext context: Context, private val sharedPreferences: HFMSharedPreference) : OAuthLoginCallback {
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
        sharedPreferences.accessToken = NaverIdLoginSDK.getAccessToken().toString()
        Timber.i(NaverIdLoginSDK.getAccessToken().toString())
        Timber.i(NaverIdLoginSDK.getRefreshToken().toString())
    }
}
