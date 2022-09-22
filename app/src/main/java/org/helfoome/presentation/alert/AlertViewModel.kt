package org.helfoome.presentation.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import org.helfoome.presentation.type.AlertType
import timber.log.Timber

class AlertViewModel : ViewModel() {

    private val _alertType = MutableLiveData<AlertType>()
    val alertType: LiveData<AlertType> get() = _alertType

    fun setAlertType(alertType: AlertType) {
        _alertType.value = alertType
    }

    fun logout() {
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
