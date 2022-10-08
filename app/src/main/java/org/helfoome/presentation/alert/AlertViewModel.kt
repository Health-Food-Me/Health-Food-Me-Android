package org.helfoome.presentation.alert

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.lifecycle.HiltViewModel
import org.helfoome.BuildConfig.*
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.presentation.type.AlertType
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(context: Application, private val storage: HFMSharedPreference) : ViewModel() {

    init {
        NaverIdLoginSDK.initialize(context, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, NAVER_CLIENT_NAME)
    }

    private val _alertType = MutableLiveData<AlertType>()
    val alertType: LiveData<AlertType> get() = _alertType

    fun setAlertType(alertType: AlertType) {
        _alertType.value = alertType
    }

    private fun clearUserData() {
        storage.clear()
    }

    fun logout() {
        clearUserData()
        NaverIdLoginSDK.logout()
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
            } else {
                Timber.i("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }
}
