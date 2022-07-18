package org.helfoome.presentation.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.helfoome.data.model.request.RequestLogin
import org.helfoome.data.local.HFMSharedPreference
import org.helfoome.domain.repository.LoginRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
//    private val sharedPreferences: HFMSharedPreference
) : ViewModel() {

    private val _social = MutableLiveData<String>()
    val social get() = _social

    fun setNaver() {
        _social.value = "naver"
    }

    fun setKakao() {
        _social.value = "kakao"
    }

    fun loginNetwork() {
        viewModelScope.launch {
            loginRepository.login(
                RequestLogin(
                    social.toString(), "Ee"
                )
            )
        }
    }
}